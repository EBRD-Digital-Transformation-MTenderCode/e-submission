package com.procurement.submission.service

import com.procurement.submission.dao.BidDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.*
import com.procurement.submission.model.dto.request.*
import com.procurement.submission.model.dto.response.*
import com.procurement.submission.model.dto.response.BidUpdateDto
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.utils.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

interface BidService {

    fun createBid(cpId: String, stage: String, owner: String, dateTime: LocalDateTime, bidDto: BidCreate): ResponseDto

    fun updateBid(cpId: String, stage: String, owner: String, token: String, bidId: String, dateTime: LocalDateTime, bidDto: BidUpdate): ResponseDto

    fun copyBids(cpId: String, newStage: String, previousStage: String, startDate: LocalDateTime, endDate: LocalDateTime, lots: LotsDto): ResponseDto

    fun getPendingBids(cpId: String, stage: String, country: String, pmd: String): ResponseDto

    fun updateStatus(cpId: String, stage: String, country: String, pmd: String, unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto

    fun updateStatusDetails(cpId: String, stage: String, bidId: String, awardStatusDetails: AwardStatusDetails): ResponseDto

    fun setFinalStatuses(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto
}

@Service
class BidServiceImpl(private val generationService: GenerationService,
                     private val rulesService: RulesService,
                     private val periodService: PeriodService,
                     private val bidDao: BidDao) : BidService {

    override fun createBid(cpId: String,
                           stage: String,
                           owner: String,
                           dateTime: LocalDateTime,
                           bidDto: BidCreate): ResponseDto {
        periodService.checkCurrentDateInPeriod(cpId, stage)
        checkRelatedLotsInDocuments(bidDto)
        processTenderers(bidDto)
        isOneRelatedLot(bidDto)
        checkTypeOfDocuments(bidDto.documents)
        checkTenderers(cpId, stage, bidDto)
        val bid = Bid(
                id = generationService.generateTimeBasedUUID().toString(),
                date = dateTime,
                status = Status.PENDING,
                statusDetails = StatusDetails.EMPTY,
                value = bidDto.value,
                documents = bidDto.documents,
                relatedLots = bidDto.relatedLots,
                tenderers = bidDto.tenderers
        )
        val entity = getEntity(
                bid = bid,
                cpId = cpId,
                stage = stage,
                owner = owner,
                token = generationService.generateRandomUUID(),
                createdDate = dateTime.toDate(),
                pendingDate = dateTime.toDate()
        )
        bidDao.save(entity)
        return getResponseDto(entity.token.toString(), bid)
    }

    override fun updateBid(cpId: String,
                           stage: String,
                           owner: String,
                           token: String,
                           bidId: String,
                           dateTime: LocalDateTime,
                           bidDto: BidUpdate): ResponseDto {
        periodService.checkCurrentDateInPeriod(cpId, stage)
        val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId))
        if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
        checkStatusesBidUpdate(bid)
        checkTypeOfDocuments(bidDto.documents)
        validateRelatedLotsOfDocuments(bidDto = bidDto, bid = bid)
        validateValue(stage = stage, bidDto = bidDto)
        bid.apply {
            date = dateTime
            status = Status.PENDING
            documents = bidDto.documents
            if (bidDto.value != null) {
                value = bidDto.value
            }
        }
        entity.jsonData = toJson(bid)
        entity.pendingDate = dateTime.toDate()
        bidDao.save(entity)
        return getResponseDto(token, bid)
    }

    override fun copyBids(cpId: String,
                          newStage: String,
                          previousStage: String,
                          startDate: LocalDateTime,
                          endDate: LocalDateTime,
                          lots: LotsDto): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, previousStage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        periodService.savePeriod(cpId, newStage, startDate, endDate)
        val mapValidEntityBid = getBidsForNewStageMap(bidEntities, lots)
        val mapCopyEntityBid = getBidsCopyMap(lots, mapValidEntityBid, newStage)
        bidDao.saveAll(mapCopyEntityBid.keys.toList())
        val bids = ArrayList(mapCopyEntityBid.values)
        return ResponseDto(true, null,
                BidsCopyResponseDto(Bids(bids), Period(startDate, endDate)))
    }

    override fun getPendingBids(cpId: String,
                                stage: String,
                                country: String,
                                pmd: String): ResponseDto {
        periodService.checkIsPeriodExpired(cpId, stage)
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val pendingBids = getPendingBids(bidEntities)
        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(pendingBids)
        val uniqueLots = getUniqueLots(relatedLotsFromBids)
        val successfulLots = getSuccessfulLots(uniqueLots, minNumberOfBids)
        val successfulBids = getSuccessfulBids(pendingBids, successfulLots)
        return ResponseDto(true, null, BidsSelectionResponseDto(successfulBids))
    }

    override fun updateStatus(cpId: String,
                              stage: String,
                              country: String,
                              pmd: String,
                              unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bids = getBidsFromEntities(bidEntities)
        val updatedBids = ArrayList<Bid>()
        bids.asSequence()
                .filter { it.status == Status.INVITED }
                .forEach { bid ->
                    bid.date = localNowUTC()
                    bid.status = Status.WITHDRAWN
                    bid.statusDetails = StatusDetails.EMPTY
                    updatedBids.add(bid)
                }
        val lotsIds = collectLots(unsuccessfulLots.unsuccessfulLots)
        bids.asSequence()
                .filter { it.relatedLots.containsAny(lotsIds) }
                .forEach { bid ->
                    bid.date = localNowUTC()
                    bid.status = Status.WITHDRAWN
                    bid.statusDetails = StatusDetails.EMPTY
                    updatedBids.add(bid)
                }
        val updatedBidEntities = getUpdatedBidEntities(bidEntities, updatedBids)
        bidDao.saveAll(updatedBidEntities)
        val period = periodService.getPeriod(cpId, stage)
        return ResponseDto(true, null,
                BidsUpdateStatusResponseDto(Period(period.startDate.toLocal(), period.endDate.toLocal()), bids))
    }

    override fun updateStatusDetails(cpId: String,
                                     stage: String,
                                     bidId: String,
                                     awardStatusDetails: AwardStatusDetails): ResponseDto {
        val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId))
        val bid = toObject(Bid::class.java, entity.jsonData)
        when (awardStatusDetails) {
            AwardStatusDetails.EMPTY -> bid.statusDetails = StatusDetails.EMPTY
            AwardStatusDetails.ACTIVE -> bid.statusDetails = StatusDetails.VALID
            AwardStatusDetails.UNSUCCESSFUL -> bid.statusDetails = StatusDetails.DISQUALIFIED
        }
        bid.date = localNowUTC()
        bidDao.save(getEntity(
                bid = bid,
                cpId = cpId,
                stage = entity.stage,
                owner = entity.owner,
                token = entity.token,
                createdDate = entity.createdDate))
        return ResponseDto(true, null, BidsUpdateStatusDetailsResponseDto(getBidUpdate(bid)))
    }

    override fun setFinalStatuses(cpId: String,
                                  stage: String,
                                  dateTime: LocalDateTime): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bids = getBidsFromEntities(bidEntities)
        for (bid in bids) {
            bid.apply {
                if (status == Status.PENDING && statusDetails != StatusDetails.EMPTY) {
                    date = dateTime
                    status = Status.fromValue(bid.statusDetails.value())
                    statusDetails = StatusDetails.EMPTY
                }
                if (bid.status == Status.PENDING && bid.statusDetails == StatusDetails.EMPTY) {
                    date = dateTime
                    status = Status.WITHDRAWN
                    statusDetails = StatusDetails.EMPTY
                }
            }
        }
        bidDao.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(true, null, BidsFinalStatusResponseDto(bids))
    }

    private fun checkStatusesBidUpdate(bid: Bid) {
        if (bid.status != Status.PENDING && bid.status != Status.INVITED) throw ErrorException(ErrorType.INVALID_STATUSES_FOR_UPDATE)
        if (bid.statusDetails != StatusDetails.EMPTY) throw ErrorException(ErrorType.INVALID_STATUSES_FOR_UPDATE)
    }

    private fun checkRelatedLotsInDocuments(bidDto: BidCreate) {
        bidDto.documents.forEach { document ->
            if (document.relatedLots != null) {
                if (!bidDto.relatedLots.containsAll(document.relatedLots))
                    throw ErrorException(ErrorType.INVALID_RELATED_LOT)
            }
        }
    }

    private fun checkTypeOfDocuments(documents: List<Document>) {
        documents.asSequence().firstOrNull { it.documentType == DocumentType.SUBMISSION_DOCUMENTS }
                ?: throw ErrorException(ErrorType.CREATE_BID_DOCUMENTS_SUBMISSION)
    }

    private fun isOneRelatedLot(bidDto: BidCreate) {
        if (bidDto.relatedLots.size > 1) throw ErrorException(ErrorType.RELATED_LOTS_MUST_BE_ONE_UNIT)
    }

    private fun validateRelatedLotsOfDocuments(bidDto: BidUpdate, bid: Bid) {
        bidDto.documents.forEach { document ->
            if (document.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots)) throw ErrorException(ErrorType.INVALID_RELATED_LOT)
            }
        }
    }

    private fun processTenderers(bidDto: BidCreate) {
        bidDto.tenderers.forEach { it.id = it.identifier.scheme + "-" + it.identifier.id }
    }

    private fun getBidsFromEntities(bidEntities: List<BidEntity>): List<Bid> {
        return bidEntities.asSequence().map { toObject(Bid::class.java, it.jsonData) }.toList()
    }

    private fun checkTenderers(cpId: String, stage: String, bidDto: BidCreate) {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isNotEmpty()) {
            val bids = getBidsFromEntities(bidEntities)
            val dtoRelatedLots = bidDto.relatedLots
            val dtoTenderers = bidDto.tenderers.asSequence().map { it.id }.toSet()
            bids.forEach { bid ->
                val bidRelatedLots = bid.relatedLots
                val bidTenderers = bid.tenderers.asSequence().map { it.id }.toSet()
                if (bidTenderers.size == bidTenderers.size &&
                        bidTenderers.containsAll(dtoTenderers) &&
                        bidRelatedLots.containsAll(dtoRelatedLots))
                    throw ErrorException(ErrorType.BID_ALREADY_WITH_LOT)
            }
        }
    }

    fun validateValue(stage: String, bidDto: BidUpdate) {
        if (stage == "EV") {
            if (bidDto.value == null) throw ErrorException(ErrorType.VALUE_IS_NULL)
        }
    }

    private fun getBidsForNewStageMap(bidEntities: List<BidEntity>, lotsDto: LotsDto): Map<BidEntity, Bid> {
        val validBids = HashMap<BidEntity, Bid>()
        val lotsIds = collectLots(lotsDto.lots)
        bidEntities.forEach { bidEntity ->
            val bid = toObject(Bid::class.java, bidEntity.jsonData)
            if (bid.status == Status.VALID && bid.statusDetails == StatusDetails.EMPTY)
                bid.relatedLots.forEach {
                    if (lotsIds.contains(it)) validBids[bidEntity] = bid
                }
        }
        return validBids
    }

    private fun getBidsCopyMap(lotsDto: LotsDto,
                               mapEntityBid: Map<BidEntity, Bid>,
                               stage: String): Map<BidEntity, Bid> {
        val bidsCopy = HashMap<BidEntity, Bid>()
        val lotsIds = collectLots(lotsDto.lots)
        mapEntityBid.forEach { map ->
            val (entity, bid) = map
            if (bid.relatedLots.containsAny(lotsIds)) {
                val bidCopy = bid.copy(
                        date = localNowUTC(),
                        status = Status.INVITED,
                        statusDetails = StatusDetails.EMPTY,
                        value = null,
                        documents = null)
                val entityCopy = getEntity(
                        bid = bidCopy,
                        cpId = entity.cpId,
                        stage = stage,
                        owner = entity.owner,
                        token = entity.token,
                        createdDate = localNowUTC().toDate())
                bidsCopy[entityCopy] = bidCopy
            }
        }
        return bidsCopy
    }

    private fun collectLots(lots: List<LotDto>?): Set<String> {
        return lots?.asSequence()?.map { it.id }?.toSet() ?: setOf()
    }

    private fun getPendingBids(entities: List<BidEntity>): List<Bid> {
        return entities.asSequence()
                .filter { it.status == Status.PENDING.value() }
                .map { toObject(Bid::class.java, it.jsonData) }
                .toList()
    }

    private fun getRelatedLotsIdFromBids(bids: List<Bid>): List<String> {
        return bids.asSequence()
                .flatMap { it.relatedLots.asSequence() }
                .toList()
    }

    private fun getUniqueLots(lots: List<String>): Map<String, Int> {
        return lots.asSequence().groupBy { it }.mapValues { it.value.size }
    }

    private fun getSuccessfulLots(uniqueLots: Map<String, Int>, minNumberOfBids: Int): List<String> {
        return uniqueLots.asSequence().filter { it.value >= minNumberOfBids }.map { it.key }.toList()
    }

    private fun getSuccessfulBids(bids: List<Bid>, successfulLots: List<String>): List<Bid> {
        return bids.asSequence()
                .filter { successfulLots.containsAny(it.relatedLots) }
                .toList()
    }

    fun getBidUpdate(bid: Bid): BidUpdateDto {
        return BidUpdateDto(
                id = bid.id,
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails,
                tenderers = createTenderers(bid.tenderers),
                value = bid.value,
                documents = bid.documents,
                relatedLots = bid.relatedLots)
    }

    private fun createTenderers(tenderers: List<OrganizationReference>?): List<OrganizationReferenceDto>? {
        return tenderers?.asSequence()
                ?.filter { it.id != null }
                ?.map { OrganizationReferenceDto(it.id!!, it.name) }
                ?.toList()
    }

    private fun getUpdatedBidEntities(bidEntities: List<BidEntity>, bids: List<Bid>): List<BidEntity> {
        val entities = ArrayList<BidEntity>()
        bidEntities.asSequence().forEach { entity ->
            bids.asSequence()
                    .firstOrNull { it.id == entity.bidId.toString() }
                    ?.let { bid ->
                        entities.add(getEntity(
                                bid = bid,
                                cpId = entity.cpId,
                                stage = entity.stage,
                                owner = entity.owner,
                                token = entity.token,
                                createdDate = entity.createdDate))
                    }
        }
        return entities
    }

    private fun getResponseDto(token: String, bid: Bid): ResponseDto {
        return ResponseDto(true, null, BidResponseDto(token, bid.id, bid))
    }

    private fun getEntity(bid: Bid,
                          cpId: String,
                          stage: String,
                          owner: String,
                          token: UUID,
                          createdDate: Date,
                          pendingDate: Date? = null): BidEntity {
        return BidEntity(
                cpId = cpId,
                stage = stage,
                owner = owner,
                status = bid.status.value(),
                bidId = UUID.fromString(bid.id),
                token = token,
                createdDate = createdDate,
                pendingDate = pendingDate,
                jsonData = toJson(bid)
        )
    }
}

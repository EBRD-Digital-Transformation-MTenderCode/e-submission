package com.procurement.submission.service

import com.google.common.base.Strings
import com.procurement.notice.exception.ErrorException
import com.procurement.notice.exception.ErrorType
import com.procurement.notice.model.bpe.ResponseDto
import com.procurement.submission.model.dto.request.LotDto
import com.procurement.submission.model.dto.request.LotsDto
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto
import com.procurement.submission.model.dto.response.*
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.model.dto.ocds.*
import com.procurement.submission.repository.BidRepository
import com.procurement.submission.utils.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

interface BidService {

    fun createBid(cpId: String, stage: String, owner: String, bidDto: Bid): ResponseDto<*>

    fun updateBid(cpId: String, stage: String, token: String, owner: String, bidDto: Bid): ResponseDto<*>

    fun copyBids(cpId: String, newStage: String, previousStage: String, startDate: LocalDateTime, endDate: LocalDateTime, lots: LotsDto): ResponseDto<*>

    fun getPendingBids(cpId: String, stage: String, country: String, pmd: String): ResponseDto<*>

    fun updateStatus(cpId: String, stage: String, country: String, pmd: String, unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto<*>

    fun updateStatusDetails(cpId: String, stage: String, bidId: String, awardStatusDetails: AwardStatusDetails): ResponseDto<*>

    fun setFinalStatuses(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto<*>
}

@Service
class BidServiceImpl(private val generationService: GenerationService,
                     private val rulesService: RulesService,
                     private val periodService: PeriodService,
                     private val bidRepository: BidRepository) : BidService {

    override fun createBid(cpId: String,
                           stage: String,
                           owner: String,
                           bidDto: Bid): ResponseDto<*> {
        periodService.checkCurrentDateInPeriod(cpId, stage)
        bidDto.apply {
            validateFieldsForCreate(this)
            checkRelatedLotsInDocuments(this)
            processTenderers(this)
            date = localNowUTC()
            id = generationService.generateTimeBasedUUID().toString()
            status = Status.PENDING
            statusDetails = StatusDetails.EMPTY
        }
        val bids = getBidsFromEntities(bidRepository.findAllByCpIdAndStage(cpId, stage))
        if (!bids.isEmpty()) checkTenderers(bids, bidDto)
        val entity = getEntity(bid = bidDto, cpId = cpId, stage = stage, owner = owner, token = generationService.generateRandomUUID())
        bidRepository.save(entity)
        return getResponseDto(entity.token.toString(), bidDto)
    }

    override fun updateBid(cpId: String,
                           stage: String,
                           token: String,
                           owner: String,
                           bidDto: Bid): ResponseDto<*> {
        validateFieldsForUpdate(bidDto)
        periodService.checkCurrentDateInPeriod(cpId, stage)
        val entity = bidRepository.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidDto.id!!))
                ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
        bid.apply {
            checkRelatedLotsAndSetDocuments(bidDto = bid, bid = this)
            updateValue(stage, bid, this)
            date = localNowUTC()
            status = Status.fromValue(bid.statusDetails!!.value())
            statusDetails = StatusDetails.EMPTY
        }
        bidRepository.save(getEntity(bid = bid, cpId = cpId, stage = stage, owner = owner, token = entity.token))
        return getResponseDto(token, bid)
    }

    override fun copyBids(cpId: String,
                          newStage: String,
                          previousStage: String,
                          startDate: LocalDateTime,
                          endDate: LocalDateTime,
                          lots: LotsDto): ResponseDto<*> {
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, previousStage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        periodService.savePeriod(cpId, newStage, startDate, endDate)
        val mapValidEntityBid = getBidsForNewStageMap(bidEntities, lots)
        val mapCopyEntityBid = getBidsCopyMap(lots, mapValidEntityBid, newStage)
        bidRepository.saveAll(mapCopyEntityBid.keys)
        val bids = ArrayList(mapCopyEntityBid.values)
        return ResponseDto(true, null,
                BidsCopyResponseDto(Bids(bids), Period(startDate, endDate)))
    }

    override fun getPendingBids(cpId: String,
                                stage: String,
                                country: String,
                                pmd: String): ResponseDto<*> {
        periodService.checkIsPeriodExpired(cpId, stage)
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage)
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
                              unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto<*> {
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage)
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
        val lotsIds = collectLots(unsuccessfulLots.lots)
        bids.asSequence()
                .filter { it.relatedLots != null }
                .filter { it.relatedLots!!.containsAny(lotsIds) }
                .forEach { bid ->
                    bid.date = localNowUTC()
                    bid.status = Status.WITHDRAWN
                    bid.statusDetails = StatusDetails.EMPTY
                    updatedBids.add(bid)
                }
        val updatedBidEntities = getUpdatedBidEntities(bidEntities, updatedBids)
        bidRepository.saveAll(updatedBidEntities)
        val period = periodService.getPeriod(cpId, stage)
        return ResponseDto(true, null,
                BidsUpdateStatusResponseDto(Period(period.startDate.toLocal(), period.endDate.toLocal()), bids))
    }

    override fun updateStatusDetails(cpId: String,
                                     stage: String,
                                     bidId: String,
                                     awardStatusDetails: AwardStatusDetails): ResponseDto<*> {
        val entity = bidRepository.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId))
                ?: throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bid = toObject(Bid::class.java, entity.jsonData)
        when (awardStatusDetails) {
            AwardStatusDetails.EMPTY -> bid.statusDetails = StatusDetails.EMPTY
            AwardStatusDetails.ACTIVE -> bid.statusDetails = StatusDetails.VALID
            AwardStatusDetails.UNSUCCESSFUL -> bid.statusDetails = StatusDetails.DISQUALIFIED
        }
        bid.date = localNowUTC()
        bidRepository.save(getEntity(bid = bid, cpId = cpId, stage = entity.stage, owner = entity.owner, token = entity.token))
        return ResponseDto(true, null, BidsUpdateStatusDetailsResponseDto(getBidUpdate(bid)))
    }

    override fun setFinalStatuses(cpId: String,
                                  stage: String,
                                  dateTime: LocalDateTime): ResponseDto<*> {
        val bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val bids = getBidsFromEntities(bidEntities)
        for (bid in bids) {
            bid.apply {
                if (status == Status.PENDING && statusDetails != StatusDetails.EMPTY) {
                    date = dateTime
                    status = Status.fromValue(bid.statusDetails!!.value())
                    statusDetails = StatusDetails.EMPTY
                }
                if (bid.status == Status.PENDING && bid.statusDetails == StatusDetails.EMPTY) {
                    date = dateTime
                    status = Status.WITHDRAWN
                    statusDetails = StatusDetails.EMPTY
                }
            }
        }
        bidRepository.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(true, null, BidsFinalStatusResponseDto(bids))
    }

    private fun validateFieldsForCreate(bidDto: Bid) {
        if (bidDto.id != null) throw ErrorException(ErrorType.ID_NOT_NULL)
    }

    private fun checkRelatedLotsInDocuments(bidDto: Bid) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null && bidDto.relatedLots != null) {
                if (!bidDto.relatedLots.containsAll(document.relatedLots)) throw ErrorException(ErrorType.INVALID_RELATED_LOT)
            }
        }
    }

    private fun checkRelatedLotsAndSetDocuments(bidDto: Bid, bid: Bid) {
        bidDto.documents?.forEach { document ->
            if (document.relatedLots != null && bid.relatedLots != null) {
                if (!bid.relatedLots.containsAll(document.relatedLots)) throw ErrorException(ErrorType.INVALID_RELATED_LOT)
            }
        }
        bidDto.documents?.let { bid.documents = it }
    }

    private fun processTenderers(bidDto: Bid) {
        bidDto.tenderers?.forEach { it.id = it.identifier.scheme + "-" + it.identifier.id }
    }

    private fun getBidsFromEntities(bidEntities: List<BidEntity>): List<Bid> {
        return bidEntities.asSequence().map { toObject(Bid::class.java, it.jsonData) }.toList()
    }

    private fun checkTenderers(bids: List<Bid>, bidDto: Bid) {
        val dtoRelatedLots = bidDto.relatedLots
        val dtoTenderers = bidDto.tenderers?.asSequence()?.map { it.id }?.toSet()
        bids.forEach { bid ->
            val bidRelatedLots = bid.relatedLots
            val bidTenderers = bid.tenderers?.asSequence()?.map { it.id }?.toSet()
            if (dtoRelatedLots != null && bidRelatedLots != null && dtoTenderers != null && bidTenderers != null) {
                if (bidTenderers.size == bidTenderers.size && bidTenderers.containsAll(dtoTenderers) && bidRelatedLots.containsAll(dtoRelatedLots))
                    throw ErrorException(ErrorType.BID_ALREADY_WITH_LOT)
            }
        }
    }

    private fun validateFieldsForUpdate(bidDto: Bid) {
        if (Strings.isNullOrEmpty(bidDto.id)) throw ErrorException(ErrorType.INVALID_ID)
        if (bidDto.statusDetails == null) throw ErrorException(ErrorType.STATUS_DETAIL_IS_NULL)
    }

    fun updateValue(stage: String, bidDto: Bid, bid: Bid) {
        if (stage == "EV") {
            if (bidDto.value == null) throw ErrorException(ErrorType.VALUE_IS_NULL)
            bid.value = bidDto.value
        }
    }

    private fun getBidsForNewStageMap(bidEntities: List<BidEntity>, lotsDto: LotsDto): Map<BidEntity, Bid> {
        val validBids = HashMap<BidEntity, Bid>()
        val lotsIds = collectLots(lotsDto.lots)
        bidEntities.forEach { bidEntity ->
            val bid = toObject(Bid::class.java, bidEntity.jsonData)
            if (bid.status == Status.VALID && bid.statusDetails == StatusDetails.EMPTY)
                bid.relatedLots?.forEach {
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
            if (bid.relatedLots != null && bid.relatedLots.containsAny(lotsIds)) {
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
                        token = entity.token)
                bidsCopy[entityCopy] = bidCopy
            }
        }
        return bidsCopy
    }

    private fun collectLots(lots: List<LotDto>?): Set<String> {
        return lots?.asSequence()?.map({ it.id })?.toSet() ?: setOf()
    }

    private fun getPendingBids(entities: List<BidEntity>): List<Bid> {
        return entities.asSequence()
                .filter { it.status == Status.PENDING.value() }
                .map { toObject(Bid::class.java, it.jsonData) }
                .toList()
    }

    private fun getRelatedLotsIdFromBids(bids: List<Bid>): List<String> {
        return bids.asSequence()
                .filter { it.relatedLots != null }
                .flatMap { it.relatedLots!!.asSequence() }.toList()
    }

    private fun getUniqueLots(lots: List<String>): Map<String, Int> {
        return lots.asSequence().groupBy { it }.mapValues { it.value.size }
    }

    private fun getSuccessfulLots(uniqueLots: Map<String, Int>, minNumberOfBids: Int): List<String> {
        return uniqueLots.asSequence().filter { it.value >= minNumberOfBids }.map { it.key }.toList()
    }

    private fun getSuccessfulBids(bids: List<Bid>, successfulLots: List<String>): List<Bid> {
        return bids.asSequence()
                .filter { it.relatedLots != null }
                .filter { successfulLots.containsAny(it.relatedLots!!) }
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
                    .first { it.id == entity.bidId.toString() }
                    .let { bid ->
                        entities.add(getEntity(
                                bid = bid,
                                cpId = entity.cpId,
                                stage = entity.stage,
                                owner = entity.owner,
                                token = entity.token))
                    }
        }
        return entities
    }

    private fun getResponseDto(token: String, bid: Bid): ResponseDto<BidResponseDto> {
        return ResponseDto(true, null, BidResponseDto(token, bid.id!!, bid))
    }


    private fun getEntity(bid: Bid,
                          cpId: String,
                          stage: String,
                          owner: String,
                          token: UUID): BidEntity {
        return BidEntity(
                cpId = cpId,
                stage = stage,
                owner = owner,
                status = bid.status!!.value(),
                bidId = UUID.fromString(bid.id!!),
                token = token,
                createdDate = bid.date!!.toDate(),
                jsonData = toJson(bid)
        )
    }
}

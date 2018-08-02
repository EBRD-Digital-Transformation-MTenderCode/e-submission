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
}

@Service
class BidServiceImpl(private val generationService: GenerationService,
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
        val lotsIds = collectLotIds(lotsDto.lots)
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
        val lotsIds = collectLotIds(lotsDto.lots)
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

    private fun collectLotIds(lots: List<LotDto>?): Set<String> {
        return lots?.asSequence()?.map { it.id }?.toSet() ?: setOf()
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

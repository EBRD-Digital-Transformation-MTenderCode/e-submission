package com.procurement.submission.service

import com.procurement.submission.dao.BidDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.*
import com.procurement.submission.model.dto.request.LotDto
import com.procurement.submission.model.dto.request.UpdateBidsByLotsRq
import com.procurement.submission.model.dto.response.*
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.utils.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

interface StatusService {

    fun bidsSelection(cpId: String, stage: String, country: String, pmd: String, dateTime: LocalDateTime): ResponseDto

    fun updateBidsByLots(cpId: String, stage: String, country: String, pmd: String, unsuccessfulLots: UpdateBidsByLotsRq): ResponseDto

    fun updateBidsByAwardStatus(cpId: String, stage: String, bidId: String, dateTime: LocalDateTime, awardStatusDetails: AwardStatusDetails): ResponseDto

    fun setFinalStatuses(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto

    fun bidWithdrawn(cpId: String, stage: String, owner: String, token: String, bidId: String, dateTime: LocalDateTime): ResponseDto

    fun bidsWithdrawn(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto

    fun prepareBidsCancellation(cpId: String, stage: String, pmd: String, phase: String, dateTime: LocalDateTime): ResponseDto

    fun bidsCancellation(cpId: String, stage: String, pmd: String, phase: String, dateTime: LocalDateTime): ResponseDto

}

@Service
class StatusServiceImpl(private val rulesService: RulesService,
                        private val periodService: PeriodService,
                        private val bidDao: BidDao) : StatusService {


    override fun bidsSelection(cpId: String,
                               stage: String,
                               country: String,
                               pmd: String,
                               dateTime: LocalDateTime): ResponseDto {
        val responseDto = BidsSelectionResponseDto(isPeriodExpired = null, tenderPeriodEndDate = null, bids = setOf())
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isNotEmpty()) {
            val pendingBids = getPendingBids(bidEntities)
            val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
            val relatedLotsFromBids = getRelatedLotsIdFromBids(pendingBids)
            val uniqueLots = getUniqueLots(relatedLotsFromBids)
            val successfulLots = getSuccessfulLotsByRule(uniqueLots, minNumberOfBids)
            val successfulBids = getBidsByRelatedLots(pendingBids, successfulLots)
            responseDto.bids = successfulBids
        }
        return ResponseDto(data = responseDto)
    }

    override fun updateBidsByLots(cpId: String,
                                  stage: String,
                                  country: String,
                                  pmd: String,
                                  unsuccessfulLots: UpdateBidsByLotsRq): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val pendingBids = getPendingBids(bidEntities)
        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val relatedLotsFromBidsList = getRelatedLotsIdFromBids(pendingBids)
        val uniqueLotsMap = getUniqueLots(relatedLotsFromBidsList)
        val successfulLotsByRuleSet = getSuccessfulLotsByRule(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsByReq = collectLotIds(unsuccessfulLots.unsuccessfulLots)
        val successfulLotsSet = successfulLotsByRuleSet.minus(unsuccessfulLotsByReq)
        val successfulBids = getBidsByRelatedLots(pendingBids, successfulLotsSet)
        val updatedBids = ArrayList<Bid>()
        successfulBids.asSequence()
                .forEach { bid ->
                    bid.date = localNowUTC()
                    updatedBids.add(bid)
                }
        val unsuccessfulBids = getBidsByRelatedLots(pendingBids, unsuccessfulLotsByReq)
        unsuccessfulBids.asSequence()
                .forEach { bid ->
                    bid.date = localNowUTC()
                    bid.status = Status.WITHDRAWN
                    bid.statusDetails = StatusDetails.EMPTY
                    updatedBids.add(bid)
                }
        val unsuccessfulLotsByRuleSet = relatedLotsFromBidsList.toSet().minus(successfulLotsByRuleSet)
        val unsuccessfulLotsSetForResp = unsuccessfulLotsByReq.minus(unsuccessfulLotsByRuleSet)
        val unsuccessfulBidsForResp = getBidsByRelatedLots(unsuccessfulBids, unsuccessfulLotsSetForResp)
        val bids = successfulBids.plus(unsuccessfulBidsForResp)
        val updatedBidEntities = getUpdatedBidEntities(bidEntities, updatedBids)
        bidDao.saveAll(updatedBidEntities)
        val period = periodService.getPeriodEntity(cpId, stage)
        return ResponseDto(
                data = BidsUpdateStatusResponseDto(
                        tenderPeriod = Period(period.startDate.toLocal(), period.endDate.toLocal()),
                        bids = bids)
        )
    }

    override fun updateBidsByAwardStatus(cpId: String,
                                         stage: String,
                                         bidId: String,
                                         dateTime: LocalDateTime,
                                         awardStatusDetails: AwardStatusDetails): ResponseDto {
        val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId))
        val bid = toObject(Bid::class.java, entity.jsonData)
        when (awardStatusDetails) {
            AwardStatusDetails.EMPTY -> bid.statusDetails = StatusDetails.EMPTY
            AwardStatusDetails.ACTIVE -> bid.statusDetails = StatusDetails.VALID
            AwardStatusDetails.UNSUCCESSFUL -> bid.statusDetails = StatusDetails.DISQUALIFIED
            AwardStatusDetails.PENDING -> TODO()
            AwardStatusDetails.CONSIDERATION -> TODO()
        }
        bid.date = dateTime
        bidDao.save(getEntity(
                bid = bid,
                cpId = cpId,
                stage = entity.stage,
                owner = entity.owner,
                token = entity.token,
                createdDate = entity.createdDate))
        return ResponseDto(data = BidResponseDto(null, null, bid))
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
        return ResponseDto(data = BidsStatusResponseDto(bids))
    }

    override fun bidWithdrawn(cpId: String,
                              stage: String,
                              owner: String,
                              token: String,
                              bidId: String,
                              dateTime: LocalDateTime): ResponseDto {
        periodService.checkCurrentDateInPeriod(cpId, stage, dateTime)
        val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId))
        if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val bid: Bid = toObject(Bid::class.java, entity.jsonData)
        checkStatusesBidUpdate(bid)
        bid.apply {
            date = dateTime
            status = Status.WITHDRAWN
        }
        entity.jsonData = toJson(bid)
        entity.status = bid.status.value()
        bidDao.save(entity)
        return ResponseDto(data = BidResponseDto(null, null, bid))

    }

    override fun bidsWithdrawn(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(data = BidsStatusResponseDto(listOf()))
        val bids = getBidsFromEntities(bidEntities)
        for (bid in bids) {
            bid.apply {
                if (status == Status.PENDING && statusDetails == StatusDetails.EMPTY) {
                    date = dateTime
                    status = Status.WITHDRAWN
                    statusDetails = StatusDetails.EMPTY
                }
            }
        }
        bidDao.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(data = BidsStatusResponseDto(bids))
    }


    override fun prepareBidsCancellation(cpId: String, stage: String, pmd: String, phase: String, dateTime: LocalDateTime): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(data = BidsStatusResponseDto(listOf()))
        val bids = getBidsFromEntities(bidEntities)
        val bidStatusPredicate = getBidStatusPredicateForPrepareCancellation(phase)
        val bidsResponseDto = mutableListOf<BidCancellation>()
        bids.asSequence()
                .filter(bidStatusPredicate)
                .forEach { bid ->
                    bid.date = dateTime
                    bid.statusDetails = StatusDetails.WITHDRAWN
                    addBidToResponseDto(bidsResponseDto, bid)
                }
        bidDao.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(data = CancellationResponseDto(bidsResponseDto))
    }

    override fun bidsCancellation(cpId: String, stage: String, pmd: String, phase: String, dateTime: LocalDateTime): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(data = BidsStatusResponseDto(listOf()))
        val bids = getBidsFromEntities(bidEntities)
        val bidStatusPredicate = getBidStatusPredicateForCancellation(phase = phase)
        val bidsResponseDto = mutableListOf<BidCancellation>()
        bids.asSequence()
                .filter(bidStatusPredicate)
                .forEach { bid ->
                    bid.date = dateTime
                    bid.status = Status.WITHDRAWN
                    bid.statusDetails = StatusDetails.EMPTY
                    addBidToResponseDto(bidsResponseDto, bid)
                }
        bidDao.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(data = CancellationResponseDto(bidsResponseDto))
    }

    private fun addBidToResponseDto(bidsResponseDto: MutableList<BidCancellation>, bid: Bid) {
        bidsResponseDto.add(BidCancellation(
                id = bid.id,
                date = bid.date,
                status = bid.status,
                statusDetails = bid.statusDetails))
    }

    private fun checkStatusesBidUpdate(bid: Bid) {
        if (bid.status != Status.PENDING && bid.status != Status.INVITED) throw ErrorException(ErrorType.INVALID_STATUSES_FOR_UPDATE)
        if (bid.statusDetails != StatusDetails.EMPTY) throw ErrorException(ErrorType.INVALID_STATUSES_FOR_UPDATE)
    }

    private fun getBidsFromEntities(bidEntities: List<BidEntity>): List<Bid> {
        return bidEntities.asSequence().map { toObject(Bid::class.java, it.jsonData) }.toList()
    }


    private fun collectLotIds(lots: List<LotDto>?): Set<String> {
        return lots?.asSequence()?.map { it.id }?.toSet() ?: setOf()
    }

    private fun getPendingBids(entities: List<BidEntity>): Set<Bid> {
        return entities.asSequence()
                .filter { it.status == Status.PENDING.value() }
                .map { toObject(Bid::class.java, it.jsonData) }
                .toSet()
    }

    private fun getRelatedLotsIdFromBids(bids: Set<Bid>): List<String> {
        return bids.asSequence()
                .flatMap { it.relatedLots.asSequence() }
                .toList()
    }

    private fun getUniqueLots(lots: List<String>): Map<String, Int> {
        return lots.asSequence().groupBy { it }.mapValues { it.value.size }
    }

    private fun getSuccessfulLotsByRule(uniqueLots: Map<String, Int>, minNumberOfBids: Int): Set<String> {
        return uniqueLots.asSequence().filter { it.value >= minNumberOfBids }.map { it.key }.toSet()
    }

    private fun getBidsByRelatedLots(bids: Set<Bid>, lots: Set<String>): Set<Bid> {
        return bids.asSequence()
                .filter { lots.containsAny(it.relatedLots) }
                .toSet()
    }

    private fun getBidStatusPredicateForPrepareCancellation(phase: String): (Bid) -> Boolean {
        when (phase) {
            "AWARDING" -> return { bid: Bid ->
                (bid.status == Status.PENDING)
                        && (bid.statusDetails == StatusDetails.EMPTY
                        || bid.statusDetails == StatusDetails.VALID
                        || bid.statusDetails == StatusDetails.DISQUALIFIED)
            }
            "TENDERING" -> return { bid: Bid ->
                (bid.status == Status.PENDING || bid.status == Status.INVITED)
                        && (bid.statusDetails == StatusDetails.EMPTY)
            }
            else -> throw ErrorException(ErrorType.CONTEXT)
        }
    }

    private fun getBidStatusPredicateForCancellation(phase: String): (Bid) -> Boolean {
        when (phase) {
            "AWARDING" -> return { bid: Bid ->
                (bid.status == Status.PENDING)
                        && (bid.statusDetails == StatusDetails.WITHDRAWN)
            }
            "TENDERING" -> return { bid: Bid ->
                (bid.status == Status.PENDING || bid.status == Status.INVITED)
                        && (bid.statusDetails == StatusDetails.WITHDRAWN)
            }
            else -> throw ErrorException(ErrorType.CONTEXT)
        }
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

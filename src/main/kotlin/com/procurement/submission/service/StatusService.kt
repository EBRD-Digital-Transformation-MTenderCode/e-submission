package com.procurement.submission.service

import com.procurement.submission.dao.BidDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.*
import com.procurement.submission.model.dto.request.LotDto
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto
import com.procurement.submission.model.dto.response.*
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.utils.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

interface StatusService {

    fun getSuccessfulBids(cpId: String, stage: String, country: String, pmd: String): ResponseDto

    fun updateStatus(cpId: String, stage: String, country: String, pmd: String, unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto

    fun updateStatusDetails(cpId: String, stage: String, bidId: String, awardStatusDetails: AwardStatusDetails): ResponseDto

    fun setFinalStatuses(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto

    fun bidsWithdrawn(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto
}

@Service
class StatusServiceImpl(private val rulesService: RulesService,
                        private val periodService: PeriodService,
                        private val bidDao: BidDao) : StatusService {


    override fun getSuccessfulBids(cpId: String,
                                   stage: String,
                                   country: String,
                                   pmd: String): ResponseDto {
        periodService.checkIsPeriodExpired(cpId, stage)
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isNotEmpty()) {
            val pendingBids = getPendingBids(bidEntities)
            val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
            val relatedLotsFromBids = getRelatedLotsIdFromBids(pendingBids)
            val uniqueLots = getUniqueLots(relatedLotsFromBids)
            val successfulLots = getSuccessfulLotsByRule(uniqueLots, minNumberOfBids)
            val successfulBids = getBidsByRelatedLots(pendingBids, successfulLots)
            return ResponseDto(true, null, BidsSelectionResponseDto(successfulBids))
        }
        return ResponseDto(true, null, BidsSelectionResponseDto(setOf()))
    }

    override fun updateStatus(cpId: String,
                              stage: String,
                              country: String,
                              pmd: String,
                              unsuccessfulLots: UnsuccessfulLotsDto): ResponseDto {
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

    override fun bidsWithdrawn(cpId: String, stage: String, dateTime: LocalDateTime): ResponseDto {
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(true, null, BidsFinalStatusResponseDto(listOf()))
        val bids = getBidsFromEntities(bidEntities)
        for (bid in bids) {
            bid.apply {
                if (status == Status.PENDING && statusDetails != StatusDetails.EMPTY) {
                    date = dateTime
                    status = Status.WITHDRAWN
                    statusDetails = StatusDetails.EMPTY
                }
            }
        }
        bidDao.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(true, null, BidsFinalStatusResponseDto(bids))
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

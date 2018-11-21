package com.procurement.submission.service

import com.procurement.submission.dao.BidDao
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.*
import com.procurement.submission.model.dto.request.*
import com.procurement.submission.model.dto.response.*
import com.procurement.submission.model.entity.BidEntity
import com.procurement.submission.utils.*
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList


@Service
class StatusService(private val rulesService: RulesService,
                    private val periodService: PeriodService,
                    private val bidDao: BidDao) {


    fun getBids(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val country = cm.context.country ?: throw ErrorException(ErrorType.CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(ErrorType.CONTEXT)

        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        val bids = HashSet<BidDto>()
        if (bidEntities.isNotEmpty()) {
            val pendingBidsSet = getPendingBids(bidEntities)
            val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
            val relatedLotsFromBidsList = getRelatedLotsListFromBids(pendingBidsSet)
            val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBidsList)
            val successfulLotsSet = getSuccessfulLotsByRule(uniqueLotsMap, minNumberOfBids)
            val successfulBidsSet = getBidsByRelatedLots(pendingBidsSet, successfulLotsSet)
            val successfulBidsIdSet = successfulBidsSet.asSequence().map { it.id }.toSet()
            for (entity in bidEntities) {
                if (entity.bidId.toString() in successfulBidsIdSet) {
                    bids.add(convertBidEntityToBidData(entity))
                }
            }
        }
        return ResponseDto(data = GetBidsRs(bids = bids))
    }

    fun getBidsAuction(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val country = cm.context.country ?: throw ErrorException(ErrorType.CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(ErrorType.CONTEXT)
        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        val ownerToBidsDataMap = HashMap<String, Set<BidDto>>()
        if (bidEntities.isNotEmpty()) {
            val pendingBidsSet = getPendingBids(bidEntities)
            val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
            val relatedLotsFromBidsList = getRelatedLotsListFromBids(pendingBidsSet)
            val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBidsList)
            val successfulLotsSet = getSuccessfulLotsByRule(uniqueLotsMap, minNumberOfBids)
            val successfulBidsSet = getBidsByRelatedLots(pendingBidsSet, successfulLotsSet)
            val successfulBidsIdSet = successfulBidsSet.asSequence().map { it.id }.toSet()
            for (entity in bidEntities) {
                if (entity.bidId.toString() in successfulBidsIdSet) {
                    val bids = ownerToBidsDataMap[entity.owner]?.toMutableSet() ?: mutableSetOf()
                    bids.add(convertBidEntityToBidData(entity))
                    ownerToBidsDataMap[entity.owner] = bids
                }
            }
        }
        val bidsData = HashSet<BidsData>()
        for (owner in ownerToBidsDataMap.keys) {
            val bids = ownerToBidsDataMap[owner] ?: setOf()
            bidsData.add(BidsData(owner, bids))
        }
        return ResponseDto(data = GetBidsAuctionRs(bidsData))
    }

    private fun convertBidEntityToBidData(entity: BidEntity): BidDto {
        val bid = toObject(Bid::class.java, entity.jsonData)
        return BidDto(
                id = bid.id,
                date = bid.date,
                createdDate = entity.createdDate.toLocal(),
                pendingDate = entity.pendingDate?.toLocal(),
                value = bid.value!!,
                tenderers = bid.tenderers,
                relatedLots = bid.relatedLots)
    }

    fun updateBidsByLots(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val country = cm.context.country ?: throw ErrorException(ErrorType.CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(ErrorType.CONTEXT)
        val awardCriteria = AwardCriteria.fromValue(cm.context.awardCriteria ?: throw ErrorException(ErrorType.CONTEXT))
        val dto = toObject(UpdateBidsByLotsRq::class.java, cm.data)

        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val pendingBidsSet = getPendingBids(bidEntities)
        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val relatedLotsFromBidsList = getRelatedLotsListFromBids(pendingBidsSet)
        val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBidsList)
        val successfulLotsByRuleSet = getSuccessfulLotsByRule(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsByReqSet = collectLotIds(dto.unsuccessfulLots)
        val successfulLotsSet = successfulLotsByRuleSet.minus(unsuccessfulLotsByReqSet)
        val successfulBidsSet = getBidsByRelatedLots(pendingBidsSet, successfulLotsSet)
        val updatedBidsList = ArrayList<Bid>()
        successfulBidsSet.asSequence()
                .forEach { bid ->
                    bid.date = localNowUTC()
                    updatedBidsList.add(bid)
                }
        val unsuccessfulBidsSet = getBidsByRelatedLots(pendingBidsSet, unsuccessfulLotsByReqSet)
        unsuccessfulBidsSet.asSequence()
                .forEach { bid ->
                    bid.date = localNowUTC()
                    bid.status = Status.WITHDRAWN
                    bid.statusDetails = StatusDetails.EMPTY
                    updatedBidsList.add(bid)
                }
        val unsuccessfulLotsByRuleSet = relatedLotsFromBidsList.minus(successfulLotsByRuleSet)
        val unsuccessfulLotsSetForRespSet = unsuccessfulLotsByReqSet.minus(unsuccessfulLotsByRuleSet)
        val unsuccessfulBidsForRespSet = getBidsByRelatedLots(unsuccessfulBidsSet, unsuccessfulLotsSetForRespSet)
        val bids = successfulBidsSet.plus(unsuccessfulBidsForRespSet)
        val updatedBidEntities = getUpdatedBidEntities(bidEntities, updatedBidsList)
        bidDao.saveAll(updatedBidEntities)
        val period = periodService.getPeriodEntity(cpId, stage)

        if (awardCriteria == AwardCriteria.PRICE_ONLY && dto.firstBids != null && dto.firstBids.isNotEmpty()) {
            val firstBidsIds = dto.firstBids.asSequence().map { it.id }.toSet()
            for (bid in bids) {
                if (bid.status == Status.PENDING && !firstBidsIds.contains(bid.id) && bid.documents != null) {
                    bid.documents = bid.documents!!.asSequence().filter {
                        it.documentType == DocumentType.SUBMISSION_DOCUMENTS || it.documentType == DocumentType.ELIGIBILITY_DOCUMENTS
                    }.toList()
                }
            }
        }
        return ResponseDto(data = BidsUpdateStatusRs(
                tenderPeriod = Period(period.startDate.toLocal(), period.endDate.toLocal()),
                bids = bids)
        )
    }

    fun updateBidsByAwardStatus(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)
        val dto = toObject(UpdateBidsByAwardStatusRq::class.java, cm.data)

        val bidId = dto.bidId
        val awardStatusDetails = AwardStatusDetails.fromValue(dto.awardStatusDetails)

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
        return ResponseDto(data = BidRs(null, null, bid))
    }

    fun setFinalStatuses(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

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
            }
        }
        bidDao.saveAll(getUpdatedBidEntities(bidEntities, bids))
        return ResponseDto(data = BidsStatusRs(bids))
    }

    fun bidWithdrawn(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(ErrorType.CONTEXT)
        val token = cm.context.token ?: throw ErrorException(ErrorType.CONTEXT)
        val bidId = cm.context.id ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

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
        return ResponseDto(data = BidRs(null, null, bid))
    }

    fun bidsWithdrawn(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(data = BidsStatusRs(listOf()))
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
        return ResponseDto(data = BidsStatusRs(bids))
    }


    fun prepareBidsCancellation(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val phase = cm.context.phase ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(data = BidsStatusRs(listOf()))
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
        return ResponseDto(data = CancellationRs(bidsResponseDto))
    }

    fun bidsCancellation(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val phase = cm.context.phase ?: throw ErrorException(ErrorType.CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(ErrorType.CONTEXT)

        val bidEntities = bidDao.findAllByCpIdAndStage(cpId, stage)
        if (bidEntities.isEmpty()) return ResponseDto(data = BidsStatusRs(listOf()))
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
        return ResponseDto(data = CancellationRs(bidsResponseDto))
    }

    fun getDocsOfConsideredBid(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val awardCriteria = AwardCriteria.fromValue(cm.context.awardCriteria ?: throw ErrorException(ErrorType.CONTEXT))
        val dto = toObject(GetDocsOfConsideredBidRq::class.java, cm.data)
        return if (awardCriteria == AwardCriteria.PRICE_ONLY) {
            val entity = bidDao.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(dto.consideredBidId))
            val bid = toObject(Bid::class.java, entity.jsonData)
            ResponseDto(data = GetDocsOfConsideredBidRs(ConsideredBid(bid.id, bid.documents)))
        } else ResponseDto(data = "")
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

    private fun getRelatedLotsListFromBids(bids: Set<Bid>): List<String> {
        return bids.asSequence()
                .flatMap { it.relatedLots.asSequence() }
                .toList()
    }

    private fun getUniqueLotsMap(lots: List<String>): Map<String, Int> {
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
            "awarding" -> return { bid: Bid ->
                (bid.status == Status.PENDING)
                        && (bid.statusDetails == StatusDetails.EMPTY
                        || bid.statusDetails == StatusDetails.VALID
                        || bid.statusDetails == StatusDetails.DISQUALIFIED)
            }
            "tendering" -> return { bid: Bid ->
                (bid.status == Status.PENDING || bid.status == Status.INVITED)
                        && (bid.statusDetails == StatusDetails.EMPTY)
            }
            else -> throw ErrorException(ErrorType.CONTEXT)
        }
    }

    private fun getBidStatusPredicateForCancellation(phase: String): (Bid) -> Boolean {
        when (phase) {
            "awarding" -> return { bid: Bid ->
                (bid.status == Status.PENDING)
                        && (bid.statusDetails == StatusDetails.WITHDRAWN)
            }
            "tendering" -> return { bid: Bid ->
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

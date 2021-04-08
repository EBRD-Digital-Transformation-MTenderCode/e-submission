package com.procurement.submission.application.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.model.data.bid.auction.get.BidsAuctionRequestData
import com.procurement.submission.application.model.data.bid.auction.get.BidsAuctionResponseData
import com.procurement.submission.application.model.data.bid.auction.get.GetBidsAuctionContext
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.AwardCriteria
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails
import com.procurement.submission.infrastructure.api.v1.CommandMessage
import com.procurement.submission.infrastructure.api.v1.ResponseDto
import com.procurement.submission.infrastructure.api.v1.cpid
import com.procurement.submission.infrastructure.api.v1.ocid
import com.procurement.submission.infrastructure.api.v1.owner
import com.procurement.submission.infrastructure.api.v1.token
import com.procurement.submission.infrastructure.handler.v1.converter.BidData
import com.procurement.submission.infrastructure.handler.v1.converter.convert
import com.procurement.submission.infrastructure.handler.v1.model.request.ConsideredBid
import com.procurement.submission.infrastructure.handler.v1.model.request.GetDocsOfConsideredBidRq
import com.procurement.submission.infrastructure.handler.v1.model.request.GetDocsOfConsideredBidRs
import com.procurement.submission.infrastructure.handler.v1.model.request.RelatedBidRq
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.utils.containsAny
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service

@Service
class StatusService(
    private val rulesService: RulesService,
    private val bidRepository: BidRepository
) {

    fun getBidsAuction(requestData: BidsAuctionRequestData, context: GetBidsAuctionContext): BidsAuctionResponseData {
        fun determineOwner(bid: Bid, bidsRecordsByIds: Map<BidId, BidEntity.Record>): BidData {
            val bidId: BidId = BidId.fromString(bid.id)
            val bidOwner: Owner = bidsRecordsByIds.getValue(bidId).owner
            return BidData(bidOwner, bid)
        }

        fun setPendingDate(
            bidsByOwner: Map.Entry<BidId, List<BidData>>,
            bidsRecordsByIds: Map<BidId, BidEntity.Record>
        ): BidsAuctionResponseData.BidsData {
            val owner = bidsByOwner.key
            val bidsWithPendingData = bidsByOwner.value
                .map { bidData ->
                    val pendingDate = bidsRecordsByIds[BidId.fromString(bidData.bid.id)]?.pendingDate
                        ?: throw ErrorException(
                            error = ErrorType.BID_NOT_FOUND,
                            message = "Cannot find bid with ${bidData.bid.id}. Bids records: ${bidsRecordsByIds.keys}."
                        )
                    bidData.bid.convert(pendingDate)
                }
            return BidsAuctionResponseData.BidsData(owner = owner, bids = bidsWithPendingData)
        }

        val bidsRecords = bidRepository.findBy(context.cpid, context.ocid)
            .orThrow { it.exception }
        val bidsRecordsByIds = bidsRecords.associateBy { it.bidId }
        val bidsDb = bidsRecordsByIds.values.map { bidRecord -> toObject(Bid::class.java, bidRecord.jsonData) }

        val bidsByRelatedLot: Map<String, List<Bid>> = bidsDb.asSequence()
            .flatMap { bid ->
                bid.relatedLots.asSequence()
                    .map { lotId ->
                        lotId to bid
                    }
            }
            .groupBy (keySelector = {it.first}, valueTransform = {it.second})

        val relatedLots = bidsByRelatedLot.keys

        // FReq-1.4.1.10
        val ignoredInRequestBids = bidsDb.filter { it.status == BidStatus.PENDING && !it.relatedLots.containsAny(relatedLots) }

        // FReq-1.4.1.2
        val notEnoughForOpeningBids = mutableSetOf<Bid>()
        val minNumberOfBids = rulesService.getRulesMinBids(context.country, context.pmd)
        val bidsForResponse = requestData.lots
            .asSequence()
            .flatMap { lot ->
                val bids = bidsByRelatedLot[lot.id.toString()]
                    ?.filter { bid -> bid.status == BidStatus.PENDING }
                    ?: emptyList()
                if (bids.size >= minNumberOfBids) {
                    bids.asSequence()
                } else {
                    notEnoughForOpeningBids.addAll(bids)
                    emptySequence()
                }
            }
            .map { bid -> determineOwner(bid, bidsRecordsByIds) }
            .groupBy { it.owner }
            .map { bidsByOwner -> setPendingDate(bidsByOwner, bidsRecordsByIds) }
            .convert()

        (ignoredInRequestBids + notEnoughForOpeningBids).asSequence()
            .map { ignoredBid -> ignoredBid.copy(statusDetails = BidStatusDetails.ARCHIVED) }
            .map { archivedBid -> updateBidRecord(archivedBid, bidsRecordsByIds) }
            .let { updatedRecord -> bidRepository.save(updatedRecord.toList()) }

        return bidsForResponse
    }

    fun getDocsOfConsideredBid(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val awardCriteria = AwardCriteria.creator(
            cm.context.awardCriteria ?: throw ErrorException(ErrorType.CONTEXT)
        )
        val dto = toObject(GetDocsOfConsideredBidRq::class.java, cm.data)
        return if (awardCriteria == AwardCriteria.PRICE_ONLY && dto.consideredBidId != null) {
            val entity = bidRepository.findBy(cpid, ocid, BidId.fromString(dto.consideredBidId))
                .orThrow { it.exception }
                ?: throw ErrorException(ErrorType.BID_NOT_FOUND)

            val bid = toObject(Bid::class.java, entity.jsonData)
            ResponseDto(data = GetDocsOfConsideredBidRs(ConsideredBid(bid.id, bid.documents)))
        } else ResponseDto(data = "")
    }

    fun checkTokenOwner(cm: CommandMessage): ResponseDto {
        val cpid = cm.cpid
        val owner = cm.owner
        val token = cm.token
        val dto = toObject(RelatedBidRq::class.java, cm.data)
        val bidIds = dto.relatedBids

        val bidEntities = bidRepository.findBy(cpid)
            .orThrow { it.exception }
        if (bidEntities.isEmpty()) throw ErrorException(ErrorType.BID_NOT_FOUND)
        val tokens = bidEntities.asSequence()
            .filter { bidIds.contains(it.bidId.toString()) }
            .map { it.token }.toSet()
        if (!tokens.contains(token)) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (bidEntities[0].owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        return ResponseDto(data = "ok")
    }

    private fun updateBidRecord(
        updatedBid: Bid,
        entityByIds: Map<BidId, BidEntity.Record>
    ): BidEntity.Updated {
        val bidId = BidId.fromString(updatedBid.id)
        val entity = entityByIds[bidId]
            ?: throw ErrorException(
                error = ErrorType.BID_NOT_FOUND,
                message = "Cannot find bid with id ${bidId}. Available bids : ${entityByIds.keys}"
            )
        return BidEntity.Updated(
            cpid = entity.cpid,
            ocid = entity.ocid,
            createdDate = entity.createdDate,
            pendingDate = entity.pendingDate,
            bid = updatedBid
        )
    }
}

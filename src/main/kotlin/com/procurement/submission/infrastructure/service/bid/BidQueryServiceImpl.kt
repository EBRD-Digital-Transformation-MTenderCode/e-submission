package com.procurement.submission.infrastructure.service.bid

import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsErrors
import com.procurement.submission.application.params.bid.query.get.GetBidsForPacsParams
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.service.BidQueryService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.response.GetBidsForPacsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import org.springframework.stereotype.Service

@Service
class BidQueryServiceImpl(
    private val bidRepository: BidRepository,
    private val transform: Transform
) : BidQueryService {

    override fun getBidsForPacs(params: GetBidsForPacsParams): Result<GetBidsForPacsResult, Fail> {
        val receivedRelatedLots = params.tender.lots.map { it.id }
        fun Bid.isRelatedLotMatched(): Boolean = this.relatedLots.any { it in receivedRelatedLots }

        val targetBids = bidRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .map { entity ->
                transform.tryDeserialization(entity.jsonData, Bid::class.java)
                    .mapFailure { Fail.Incident.Database.DatabaseParsing(exception = it.exception) }
                    .onFailure { return it }
            }
            .filter { it.isRelatedLotMatched() }

        val targetBidsRelatedLots = targetBids.map { it.relatedLots }.flatten()

        if (!targetBidsRelatedLots.containsAll(receivedRelatedLots))
            return GetBidsForPacsErrors.BidsNotFound(receivedRelatedLots - targetBidsRelatedLots).asFailure()
        else
            return targetBids
                .map { GetBidsForPacsResult.ResponseConverter.fromDomain(it) }
                .let { GetBidsForPacsResult(bids = GetBidsForPacsResult.Bids(details = it)) }
                .asSuccess()
    }

}
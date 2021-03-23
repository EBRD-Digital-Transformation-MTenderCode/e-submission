package com.procurement.submission.application.params.bid

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.bid.BidId

sealed class FinalizeBidsByAwardsErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound(ids: Collection<BidId>) : FinalizeBidsByAwardsErrors(
        numberError = "4.15.1",
        description = "Cannot find bids by ids specified in request. Ids: ${ids}"
    )
}
package com.procurement.submission.application.params.bid.query.find

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.bid.BidId

sealed class FindDocumentsByBidIdsErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound(bidsIds: Collection<BidId>) : FindDocumentsByBidIdsErrors(
        numberError = "13.12.1",
        description = "Cannot find bids by ids specified in request. Ids: ${bidsIds}"
    )
}
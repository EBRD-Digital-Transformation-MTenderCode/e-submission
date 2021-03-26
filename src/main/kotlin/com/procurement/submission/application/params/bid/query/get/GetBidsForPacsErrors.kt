package com.procurement.submission.application.params.bid.query.get

import com.procurement.submission.domain.fail.Fail

sealed class GetBidsForPacsErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound(bidsIds: List<String>) : GetBidsForPacsErrors(
        numberError = "13.10.1",
        description = "Cannot find bids by ids specified in request. Ids: $bidsIds"
    )
}
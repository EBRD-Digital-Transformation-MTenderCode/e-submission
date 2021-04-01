package com.procurement.submission.application.params.errors

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.bid.BidId

sealed class CreateInvitationsErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound(ids: Collection<BidId>) : CreateInvitationsErrors(
        numberError = "13.18.1",
        description = "Cannot find bids by ids specified in request. Ids: $ids"
    )
}
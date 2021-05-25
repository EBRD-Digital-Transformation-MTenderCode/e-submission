package com.procurement.submission.application.params.errors

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

sealed class GetSuppliersOwnersErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {

    override val code: String = prefix + numberError

    class BidsNotFound(cpid: Cpid, ocid: Ocid) : GetSuppliersOwnersErrors(
        numberError = "13.21.1",
        description = "Bids by cpid '$cpid' and ocid '$ocid' is not found."
    )

    class BidNotFound() : GetSuppliersOwnersErrors(
        numberError = "13.21.2",
        description = "Bid not found"
    )
}
package com.procurement.submission.application.params.errors

import com.procurement.submission.domain.fail.Fail

sealed class CreateInvitationsErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound() : CreateInvitationsErrors(
        numberError = "13.18.1",
        description = "Cannot find bids by specified lots."
    )
}
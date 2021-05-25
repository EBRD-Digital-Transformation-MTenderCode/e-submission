package com.procurement.submission.application.params.errors

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

sealed class PersonsProcessingErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound(cpid: Cpid, ocid: Ocid) : PersonsProcessingErrors(
        numberError = "13.22.1",
        description = "Bids by cpid '$cpid' and ocid '$ocid' is not found."
    )

    class OrganizationNotFound(candidateId: String) : PersonsProcessingErrors(
        numberError = "13.22.2",
        description = "Organization by id '$candidateId' not found."
    )
}
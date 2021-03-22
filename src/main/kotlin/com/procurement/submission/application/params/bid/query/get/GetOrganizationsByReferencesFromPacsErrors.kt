package com.procurement.submission.application.params.bid.query.get

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

sealed class GetOrganizationsByReferencesFromPacsErrors(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class BidsNotFound(cpid: Cpid, ocid: Ocid) : GetOrganizationsByReferencesFromPacsErrors(
        numberError = "13.16.1",
        description = "No bids found by cpid '$cpid' and ocid '$ocid'."
    )

    class OrganizationNotFound(id: String) : GetOrganizationsByReferencesFromPacsErrors(
        numberError = "13.16.2",
        description = "Cannot find organization by id '$id'."
    )
}
package com.procurement.submission.application.params.bid.query.get

import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate

class GetOrganizationsByReferencesFromPacsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val parties: List<Party>
) {
    companion object {
        private const val PARTIES_ATTRIBUTE_NAME = "parties"

        fun tryCreate(cpid: String, ocid: String, parties: List<Party>): Result<GetOrganizationsByReferencesFromPacsParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid)
                .onFailure { return it }

            val parsedOcid = parseOcid(value = ocid)
                .onFailure { return it }

            parties.validate(notEmptyRule(PARTIES_ATTRIBUTE_NAME))
                .onFailure { return it }

            return GetOrganizationsByReferencesFromPacsParams(cpid = parsedCpid, ocid = parsedOcid, parties = parties).asSuccess()
        }
    }

    data class Party(val id: String)
}
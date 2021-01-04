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

class GetBidsForPacsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender
) {
    companion object {
        private const val LOTS_ATTRIBUTE_NAME = "lots"

        fun tryCreate(cpid: String, ocid: String, tender: Tender): Result<GetBidsForPacsParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid)
                .onFailure { return it }

            val parsedOcid = parseOcid(value = ocid)
                .onFailure { return it }

            tender.lots.validate(notEmptyRule(LOTS_ATTRIBUTE_NAME))
                .onFailure { return it }

            return GetBidsForPacsParams(cpid = parsedCpid, ocid = parsedOcid, tender = tender).asSuccess()
        }
    }

    data class Tender(
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: String
        )
    }
}
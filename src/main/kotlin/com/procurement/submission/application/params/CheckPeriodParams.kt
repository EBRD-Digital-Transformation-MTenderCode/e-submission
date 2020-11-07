package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import java.time.LocalDateTime

class CheckPeriodParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val date: LocalDateTime
) {
    companion object {

        fun tryCreate(
            cpid: String,
            ocid: String,
            date: String
        ): Result<CheckPeriodParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .onFailure { return it }

            val ocidParsed = parseOcid(value = ocid)
                .onFailure { return it }

            val dateParsed = parseDate(value = date)
                .onFailure { return it }

            return CheckPeriodParams(cpidParsed, ocidParsed, dateParsed).asSuccess()
        }
    }
}
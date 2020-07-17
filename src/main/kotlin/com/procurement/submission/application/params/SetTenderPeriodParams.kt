package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import java.time.LocalDateTime

class SetTenderPeriodParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val date: LocalDateTime,
    val tender: Tender
) {
    companion object {

        fun tryCreate(
            cpid: String,
            ocid: String,
            date: String,
            tender: Tender
        ): Result<SetTenderPeriodParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { error -> return error }

            val ocidParsed = parseOcid(value = ocid)
                .orForwardFail { error -> return error }

            val dateParsed = parseDate(value = date, attributeName = "date")
                .orForwardFail { error -> return error }

            return SetTenderPeriodParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                date = dateParsed,
                tender = tender
            ).asSuccess()
        }
    }

    data class Tender(
        val tenderPeriod: TenderPeriod
    ) {
        class TenderPeriod private constructor(
            val endDate: LocalDateTime
        ) {
            companion object {
                private const val TENDER_PERIOD_END_DATE_ATTRIBUTE_NAME = "tender.tenderPeriod.endDate"

                fun tryCreate(endDate: String): Result<TenderPeriod, DataErrors> {
                    val endDateParsed = parseDate(endDate, TENDER_PERIOD_END_DATE_ATTRIBUTE_NAME)
                        .orForwardFail { fail -> return fail }

                    return TenderPeriod(endDateParsed).asSuccess()
                }
            }
        }
    }
}
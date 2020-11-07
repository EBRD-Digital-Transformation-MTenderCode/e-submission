package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
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
                .onFailure { return it }

            val ocidParsed = parseOcid(value = ocid)
                .onFailure { return it }

            val dateParsed = parseDate(value = date, attributeName = "date")
                .onFailure { return it }

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
                        .onFailure { return it }

                    return TenderPeriod(endDateParsed).asSuccess()
                }
            }
        }
    }
}
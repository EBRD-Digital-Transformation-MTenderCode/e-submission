package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import java.time.LocalDateTime

data class SetTenderPeriodParams(
    val cpid: Cpid,
    val date: LocalDateTime,
    val tender: Tender
) {
    companion object {

        fun tryCreate(
            cpid: String,
            date: String,
            tender: Tender
        ): Result<SetTenderPeriodParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { error -> return error }

            val dateParsed = parseDate(value = date, attributeName = "date")
                .orForwardFail { error -> return error }

            return SetTenderPeriodParams(
                cpid = cpidParsed,
                date = dateParsed,
                tender = tender
            ).asSuccess()
        }
    }

    data class Tender(
        val tenderPeriod: TenderPeriod
    ) {
        data class TenderPeriod(
            val endDate: String
        ){
            companion object{
                fun tryCreate(): Result
            }
        }
    }
}
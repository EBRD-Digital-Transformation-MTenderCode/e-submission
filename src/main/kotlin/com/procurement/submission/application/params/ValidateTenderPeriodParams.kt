package com.procurement.submission.application.params

import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import java.time.LocalDateTime

class ValidateTenderPeriodParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val date: LocalDateTime,
    val country: String,
    val pmd: ProcurementMethod,
    val operationType: OperationType,
    val tender: Tender
) {
    companion object {

        val allowedPmd = ProcurementMethod.values()
            .filter {
                when (it) {
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT -> true

                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV -> false

                }
            }.toSet()

        val allowedOperationTypes = OperationType.allowedElements
            .filter {
                when (it) {
                    OperationType.START_SECOND_STAGE -> true
                    OperationType.QUALIFICATION_PROTOCOL -> false
                }
            }.toSet()

        fun tryCreate(
            cpid: String,
            ocid: String,
            date: String,
            country: String,
            pmd: String,
            operationType: String,
            tender: Tender
        ): Result<ValidateTenderPeriodParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { error -> return error }

            val ocidParsed = parseOcid(value = ocid)
                .orForwardFail { error -> return error }

            val dateParsed = parseDate(value = date, attributeName = "date")
                .orForwardFail { error -> return error }

            val pmdParsed = parsePmd(value = pmd, allowedEnums = allowedPmd)
                .orForwardFail { error -> return error }

            val operationTypeParsed = parseOperationType(value = operationType, allowedEnums = allowedOperationTypes)
                .orForwardFail { error -> return error }

            return ValidateTenderPeriodParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                pmd = pmdParsed,
                date = dateParsed,
                country = country,
                tender = tender,
                operationType = operationTypeParsed
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
                private val TENDER_PERIOD_END_DATE_ATTRIBUTE = "tender.tenderPeriod.endDate"

                fun tryCreate(endDate: String): Result<TenderPeriod, DataErrors> {
                    val dateParsed = parseDate(endDate, TENDER_PERIOD_END_DATE_ATTRIBUTE)
                        .orForwardFail { error -> return error }

                    return TenderPeriod(dateParsed).asSuccess()
                }
            }
        }
    }
}
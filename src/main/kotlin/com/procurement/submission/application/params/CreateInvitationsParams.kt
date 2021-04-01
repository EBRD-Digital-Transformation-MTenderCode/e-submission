package com.procurement.submission.application.params

import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.tryUUID
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.lot.LotId
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate
import java.time.LocalDateTime

class CreateInvitationsParams private constructor(
    val cpid: Cpid,
    val date: LocalDateTime,
    val additionalCpid: Cpid,
    val additionalOcid: Ocid,
    val tender: Tender
) {
    companion object {

        fun tryCreate(
            cpid: String,
            additionalCpid: String,
            additionalOcid: String,
            date: String,
            tender: Tender,
        ): Result<CreateInvitationsParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid)
                .onFailure { return it }

            val parsedAdditionalCpid = parseCpid(value = additionalCpid)
                .onFailure { return it }

            val parsedAdditionalOcid = parseOcid(value = additionalOcid)
                .onFailure { return it }

            val parsedDate = parseDate(value = date)
                .onFailure { return it }

            return CreateInvitationsParams(
                cpid = parsedCpid,
                additionalCpid = parsedAdditionalCpid,
                additionalOcid = parsedAdditionalOcid,
                date = parsedDate,
                tender = tender
            ).asSuccess()
        }
    }

    class Tender private constructor(
        val lots: List<Lot>
    ) {

        companion object {
            private const val LOTS_ATTRIBUTE_NAME = "lots"

            fun tryCreate(lots: List<Lot>): Result<Tender, DataErrors.Validation> {
                lots.validate(notEmptyRule(LOTS_ATTRIBUTE_NAME))
                    .onFailure { return it }

                return Tender(lots = lots).asSuccess()
            }
        }

        class Lot private constructor(val id: LotId) {
            companion object {
                fun tryCreate(id: String): Result<Lot, Fail.Incident.Transform.Parsing> {
                    val parsedId = id.tryUUID().onFailure { return it }

                    return Lot(id = parsedId).asSuccess()
                }
            }

        }
    }

}
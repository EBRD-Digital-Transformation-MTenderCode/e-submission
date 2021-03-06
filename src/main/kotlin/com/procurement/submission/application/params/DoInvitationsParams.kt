package com.procurement.submission.application.params

import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.EnumElementProvider.Companion.keysAsStrings
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.enums.QualificationStatusDetails
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate
import java.time.LocalDateTime

class DoInvitationsParams private constructor(
    val cpid: Cpid,
    val date: LocalDateTime,
    val country: String,
    val pmd: ProcurementMethod,
    val operationType: OperationType,
    val qualifications: List<Qualification>,
    val submissions: Submissions?
) {
    companion object {
        private const val QUALIFICATIONS_ATTRIBUTE_NAME = "qualifications"
        private const val DATE_ATTRIBUTE_NAME = "date"
        private val ALLOWED_PMD = ProcurementMethod.values().toSet()
        private val ALLOWED_OPERATION_TYPE = OperationType.values().toSet()

        fun tryCreate(
            cpid: String,
            date: String,
            country: String,
            pmd: String,
            operationType: String,
            qualifications: List<Qualification>?,
            submissions: Submissions?
        ): Result<DoInvitationsParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .onFailure { return it }

            val dateParsed = parseDate(value = date, attributeName = DATE_ATTRIBUTE_NAME)
                .onFailure { return it }

            val pmdParsed = ProcurementMethod.orNull(pmd)
                ?: return Result.failure(
                    DataErrors.Validation.UnknownValue(
                        name = "pmd",
                        expectedValues = ALLOWED_PMD.map { it.name },
                        actualValue = pmd
                    )
                )

            val operationTypeParsed = OperationType.orNull(operationType)
                ?: return Result.failure(
                    DataErrors.Validation.UnknownValue(
                        name = "operationType",
                        expectedValues = ALLOWED_OPERATION_TYPE.keysAsStrings(),
                        actualValue = operationType
                    )
                )

            qualifications.validate(
                notEmptyRule(
                    QUALIFICATIONS_ATTRIBUTE_NAME
                )
            )
                .onFailure { return it }

            return DoInvitationsParams(
                cpid = cpidParsed,
                date = dateParsed,
                country = country,
                pmd = pmdParsed,
                operationType = operationTypeParsed,
                qualifications = qualifications ?: emptyList(),
                submissions = submissions
            ).asSuccess()
        }
    }

    class Qualification private constructor(
        val id: QualificationId,
        val statusDetails: QualificationStatusDetails,
        val relatedSubmission: SubmissionId
    ) {
        companion object {
            private const val QUALIFICATIONS_STATUS_DETAILS_ATTRIBUTE_NAME = "qualifications.statusDetails"
            private const val QUALIFICATIONS_ID_ATTRIBUTE_NAME = "qualifications.id"

            private val allowedStatusDetails = QualificationStatusDetails.allowedElements
                .filter {
                    when (it) {
                        QualificationStatusDetails.ACTIVE -> true
                        QualificationStatusDetails.UNSUCCESSFUL,
                        QualificationStatusDetails.AWAITING,
                        QualificationStatusDetails.CONSIDERATION -> false
                    }
                }.toSet()

            fun tryCreate(
                id: String,
                statusDetails: String,
                relatedSubmission: String
            ): Result<Qualification, DataErrors> {
                val idParsed = parseQualificationId(value = id, attributeName = QUALIFICATIONS_ID_ATTRIBUTE_NAME)
                    .onFailure { return it }

                val statusDetailsParsed = parseQualificationStatusDetails(
                    value = statusDetails,
                    attributeName = QUALIFICATIONS_STATUS_DETAILS_ATTRIBUTE_NAME,
                    allowedEnums = allowedStatusDetails
                ).onFailure { return it }

                return Qualification(
                    id = idParsed,
                    statusDetails = statusDetailsParsed,
                    relatedSubmission = SubmissionId.create(relatedSubmission)
                ).asSuccess()
            }
        }
    }

    class Submissions private constructor(
        val details: List<Detail>
    ) {
        companion object {
            private const val SUBMISSIONS_DETAILS_ATTRIBUTE_NAME = "submissions.details"

            fun tryCreate(details: List<Detail>): Result<Submissions, DataErrors> {
                details.validate(notEmptyRule(SUBMISSIONS_DETAILS_ATTRIBUTE_NAME))
                    .onFailure { return it }
                return Submissions(details).asSuccess()
            }
        }

        class Detail private constructor(
            val id: SubmissionId,
            val candidates: List<Candidate>
        ) {
            companion object {
                private const val SUBMISSIONS_DETAILS_CANDIDATES_ATTRIBUTE_NAME = "submissions.details.candidates"

                fun tryCreate(
                    id: String, candidates: List<Candidate>
                ): Result<Detail, DataErrors> {
                    candidates.validate(notEmptyRule(SUBMISSIONS_DETAILS_CANDIDATES_ATTRIBUTE_NAME))
                        .onFailure { return it }

                    return Detail(id = SubmissionId.create(id), candidates = candidates).asSuccess()
                }
            }

            data class Candidate(
                val id: String,
                val name: String
            )
        }
    }
}
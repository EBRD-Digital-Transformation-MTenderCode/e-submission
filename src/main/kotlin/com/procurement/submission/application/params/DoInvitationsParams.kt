package com.procurement.submission.application.params

import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.domain.functional.asSuccess
import com.procurement.submission.domain.functional.validate
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.QualificationStatusDetails
import com.procurement.submission.domain.model.qualification.QualificationId
import com.procurement.submission.domain.model.submission.SubmissionId
import java.time.LocalDateTime

class DoInvitationsParams private constructor(
    val cpid: Cpid,
    val date: LocalDateTime,
    val qualifications: List<Qualification>,
    val submissions: Submissions
) {
    companion object {
        private const val QUALIFICATIONS_ATTRIBUTE_NAME = "qualifications"
        private const val DATE_ATTRIBUTE_NAME = "date"

        fun tryCreate(
            cpid: String,
            date: String,
            qualifications: List<Qualification>,
            submissions: Submissions
        ): Result<DoInvitationsParams, DataErrors> {
            val cpidParsed = parseCpid(value = cpid)
                .orForwardFail { fail -> return fail }

            val dateParsed = parseDate(value = date, attributeName = DATE_ATTRIBUTE_NAME)
                .orForwardFail { fail -> return fail }

            qualifications.validate(
                notEmptyRule(
                    QUALIFICATIONS_ATTRIBUTE_NAME
                )
            )
                .orForwardFail { fail -> return fail }

            return DoInvitationsParams(
                cpid = cpidParsed,
                date = dateParsed,
                qualifications = qualifications,
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
                        QualificationStatusDetails.ACTIVE,
                        QualificationStatusDetails.UNSUCCESSFUL -> true
                        QualificationStatusDetails.AWAITING,
                        QualificationStatusDetails.CONSIDERATION -> false
                    }
                }

            fun tryCreate(
                id: String,
                statusDetails: String,
                relatedSubmission: String
            ): Result<Qualification, DataErrors> {
                val idParsed = parseQualificationId(value = id, attributeName = QUALIFICATIONS_ID_ATTRIBUTE_NAME)
                    .orForwardFail { fail -> return fail }

                val statusDetailsParsed = parseQualificationStatusDetails(
                    value = statusDetails,
                    attributeName = QUALIFICATIONS_STATUS_DETAILS_ATTRIBUTE_NAME,
                    allowedEnums = allowedStatusDetails
                ).orForwardFail { fail -> return fail }

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
                    .orForwardFail { fail -> return fail }
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
                        .orForwardFail { fail -> return fail }

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
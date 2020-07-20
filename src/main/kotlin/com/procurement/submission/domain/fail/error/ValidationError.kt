package com.procurement.submission.domain.fail.error

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.enums.OperationType
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.submission.SubmissionId
import java.time.Duration

sealed class ValidationError(
    numberError: String,
    override val description: String,
    val entityId: String? = null
) : Fail.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class MissingSubmission(
        submissionIds: Collection<SubmissionId>
    ) : ValidationError(
        numberError = "13.1.2",
        description = "Missing submission(s) by id(s) '${submissionIds.joinToString()}'."
    )

    class ActiveInvitationsFound(
        invitations: Collection<InvitationId>
    ) : ValidationError(
        numberError = "13.2.1",
        description = "Active invitations was found: invitation(s) by id(s): '${invitations.joinToString()}'."
    )

    sealed class EntityNotFound(description: String) : ValidationError("17", description) {

        class TenderPeriodRule(
            country: String,
            pmd: ProcurementMethod,
            parameter: String,
            operationType: OperationType
        ) : EntityNotFound("Tender period rule '$parameter' not found by country '$country', pmd '${pmd.name}', operationType '$operationType'.")
    }

    class TenderPeriodDurationError(expectedDuration: Duration): ValidationError(
        numberError = "1.17.2",
        description = "Actual tender period duration is less than '${expectedDuration.toDays()}' days."
    )
}
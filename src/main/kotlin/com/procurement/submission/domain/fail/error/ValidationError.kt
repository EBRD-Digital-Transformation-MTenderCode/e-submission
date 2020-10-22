package com.procurement.submission.domain.fail.error

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.InvitationStatus
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

        class ReturnInvitationsRule(
            country: String,
            pmd: ProcurementMethod,
            parameter: String,
            operationType: OperationType?
        ) : EntityNotFound("Invitations rule '$parameter' not found by country '$country', pmd '${pmd.name}', operationType '$operationType'.")
    }

    class TenderPeriodDurationError(expectedDuration: Duration): ValidationError(
        numberError = "13.4.2",
        description = "Actual tender period duration is less than '${expectedDuration.toDays()}' days."
    )

    class PendingInvitationsNotFoundOnPublishInvitations(cpid: Cpid) :
        ValidationError(
            numberError = "13.3.1",
            description = "Invitations in status '${InvitationStatus.PENDING}' was not found by cpid = '$cpid'"
        )

    class TenderPeriodNotFound(cpid: Cpid, ocid: Ocid) :
        ValidationError(
            numberError = "13.6.1",
            description = "Tender period by cpid '$cpid' and ocid '$ocid' not found."
        )

    class ReceivedDatePrecedesStoredStartDate() :
        ValidationError(
            numberError = "13.6.2",
            description = "Received date must be after stored start date."
        )

    class ReceivedDateIsAfterStoredEndDate() :
        ValidationError(
            numberError = "13.6.3",
            description = "Received date must precede stored end date."
        )
}
package com.procurement.submission.domain.fail.error

import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.submission.SubmissionId

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

    class PendingInvitationsNotFoundOnPublishInvitations(cpid: Cpid) :
        ValidationError(
            numberError = "13.3.1",
            description = "Invitations in status '${InvitationStatus.PENDING}' was not found by cpid = '$cpid'"
        )
}
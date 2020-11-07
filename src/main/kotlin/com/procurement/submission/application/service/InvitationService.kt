package com.procurement.submission.application.service

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsResult
import com.procurement.submission.infrastructure.dto.invitation.publish.PublishInvitationsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.ValidationResult

interface InvitationService {

    fun doInvitations(params: DoInvitationsParams): Result<DoInvitationsResult?, Fail>

    fun checkAbsenceActiveInvitations(params: CheckAbsenceActiveInvitationsParams): ValidationResult<Fail>

    fun publishInvitations(params: PublishInvitationsParams): Result<PublishInvitationsResult, Fail>
}
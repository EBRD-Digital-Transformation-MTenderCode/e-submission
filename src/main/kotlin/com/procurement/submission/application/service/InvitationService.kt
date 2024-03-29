package com.procurement.submission.application.service

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.application.params.CheckExistenceOfInvitationParams
import com.procurement.submission.application.params.CreateInvitationsParams
import com.procurement.submission.application.params.DoInvitationsParams
import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.response.CreateInvitationsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.DoInvitationsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.PublishInvitationsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.Validated

interface InvitationService {

    fun doInvitations(params: DoInvitationsParams): Result<DoInvitationsResult?, Fail>

    fun checkAbsenceActiveInvitations(params: CheckAbsenceActiveInvitationsParams): Validated<Fail>

    fun publishInvitations(params: PublishInvitationsParams): Result<PublishInvitationsResult, Fail>

    fun createInvitations(params: CreateInvitationsParams): Result<CreateInvitationsResult, Fail>

    fun checkExistenceOfInvitation(params: CheckExistenceOfInvitationParams): Validated<Fail>
}
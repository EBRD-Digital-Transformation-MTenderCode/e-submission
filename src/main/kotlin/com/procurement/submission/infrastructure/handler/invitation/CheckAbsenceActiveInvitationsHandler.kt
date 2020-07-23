package com.procurement.submission.infrastructure.handler.invitation

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.InvitationServiceImpl
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.ValidationResult
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.invitation.create.CheckAbsenceActiveInvitationsRequest
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractValidationHandler2
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import org.springframework.stereotype.Component

@Component
class CheckAbsenceActiveInvitationsHandler(
    val transform: Transform,
    logger: Logger,
    private val invitationService: InvitationServiceImpl
) : AbstractValidationHandler2<Command2Type, Fail>(logger = logger) {

    override val action: Command2Type = Command2Type.CHECK_ABSENCE_ACTIVE_INVITATIONS

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node.tryGetParams(CheckAbsenceActiveInvitationsRequest::class.java, transform = transform)
            .doReturn { fail -> return ValidationResult.error(fail) }
            .convert()
            .doReturn { fail -> return ValidationResult.error(fail) }

        return invitationService.checkAbsenceActiveInvitations(params)
    }
}
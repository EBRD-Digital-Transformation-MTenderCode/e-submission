package com.procurement.submission.infrastructure.handler.invitation

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.InvitationServiceImpl
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.invitation.check.CheckAbsenceActiveInvitationsRequest
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractValidationHandler2
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import com.procurement.submission.lib.functional.Validated
import org.springframework.stereotype.Component

@Component
class CheckAbsenceActiveInvitationsHandler(
    val transform: Transform,
    logger: Logger,
    private val invitationService: InvitationServiceImpl
) : AbstractValidationHandler2<Command2Type, Fail>(logger = logger) {

    override val action: Command2Type = Command2Type.CHECK_ABSENCE_ACTIVE_INVITATIONS

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node.tryGetParams(CheckAbsenceActiveInvitationsRequest::class.java, transform = transform)
            .onFailure { return Validated.error(it.reason) }
            .convert()
            .onFailure { return Validated.error(it.reason) }

        return invitationService.checkAbsenceActiveInvitations(params)
    }
}
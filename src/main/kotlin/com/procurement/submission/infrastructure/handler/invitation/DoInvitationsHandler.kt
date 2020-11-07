package com.procurement.submission.infrastructure.handler.invitation

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.repository.HistoryRepository
import com.procurement.submission.application.service.InvitationServiceImpl
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsRequest
import com.procurement.submission.infrastructure.dto.invitation.create.DoInvitationsResult
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractHistoricalHandler2
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import com.procurement.submission.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class DoInvitationsHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val invitationService: InvitationServiceImpl
) : AbstractHistoricalHandler2<Command2Type, DoInvitationsResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = DoInvitationsResult::class.java,
    transform = transform
) {
    override val action: Command2Type = Command2Type.DO_INVITATIONS

    override fun execute(node: JsonNode): Result<DoInvitationsResult?, Fail> {
        val params = node.tryGetParams(DoInvitationsRequest::class.java, transform = transform)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return invitationService.doInvitations(params)
    }
}
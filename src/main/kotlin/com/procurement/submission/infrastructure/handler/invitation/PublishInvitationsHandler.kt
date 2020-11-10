package com.procurement.submission.infrastructure.handler.invitation

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.InvitationServiceImpl
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.invitation.publish.PublishInvitationsRequest
import com.procurement.submission.infrastructure.dto.invitation.publish.PublishInvitationsResult
import com.procurement.submission.infrastructure.handler.AbstractHistoricalHandlerV2
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class PublishInvitationsHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val invitationService: InvitationServiceImpl
) : AbstractHistoricalHandlerV2<CommandTypeV2, PublishInvitationsResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = PublishInvitationsResult::class.java,
    transform = transform
) {
    override val action: CommandTypeV2 = CommandTypeV2.PUBLISH_INVITATIONS

    override fun execute(node: JsonNode): Result<PublishInvitationsResult, Fail> {
        val params = node.tryGetParams(PublishInvitationsRequest::class.java, transform = transform)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return invitationService.publishInvitations(params)
    }
}
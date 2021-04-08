package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.InvitationService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.CreateInvitationsRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.CreateInvitationsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateInvitationsHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val invitationService: InvitationService
) : AbstractHistoricalHandlerV2<CommandTypeV2, CreateInvitationsResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = CreateInvitationsResult::class.java,
    transform = transform
) {
    override val action: CommandTypeV2 = CommandTypeV2.CREATE_INVITATIONS

    override fun execute(node: JsonNode): Result<CreateInvitationsResult, Fail> {
        val params = node.tryGetParams(CreateInvitationsRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it }

        return invitationService.createInvitations(params)
    }
}
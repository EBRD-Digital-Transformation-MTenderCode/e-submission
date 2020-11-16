package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.InvitationServiceImpl
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.DoInvitationsRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.DoInvitationsResult
import com.procurement.submission.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class DoInvitationsHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val invitationService: InvitationServiceImpl
) : AbstractHistoricalHandlerV2<CommandTypeV2, DoInvitationsResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = DoInvitationsResult::class.java,
    transform = transform
) {
    override val action: CommandTypeV2 = CommandTypeV2.DO_INVITATIONS

    override fun execute(node: JsonNode): Result<DoInvitationsResult?, Fail> {
        val params = node.tryGetParams(DoInvitationsRequest::class.java, transform = transform)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return invitationService.doInvitations(params)
    }
}
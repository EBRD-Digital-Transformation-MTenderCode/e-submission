package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.FinalizeBidsByAwardsRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.FinalizeBidsByAwardsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class FinalizeBidsByAwardsHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val bidService: BidService
) : AbstractHistoricalHandlerV2<CommandTypeV2, FinalizeBidsByAwardsResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = FinalizeBidsByAwardsResult::class.java,
    transform = transform
) {
    override val action: CommandTypeV2 = CommandTypeV2.FINALIZE_BIDS_BY_AWARDS

    override fun execute(node: JsonNode): Result<FinalizeBidsByAwardsResult, Fail> {
        val params = node.tryGetParams(FinalizeBidsByAwardsRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it }

        return bidService.finalizeBidsByAwards(params)
    }
}
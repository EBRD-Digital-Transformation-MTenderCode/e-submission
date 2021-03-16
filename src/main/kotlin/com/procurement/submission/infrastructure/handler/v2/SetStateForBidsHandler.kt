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
import com.procurement.submission.infrastructure.handler.v2.model.request.SetStateForBidsRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.SetStateForBidsResult
import com.procurement.submission.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class SetStateForBidsHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val bidService: BidService
) : AbstractHistoricalHandlerV2<CommandTypeV2, SetStateForBidsResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = SetStateForBidsResult::class.java,
    transform = transform
) {
    override val action: CommandTypeV2 = CommandTypeV2.SET_STATE_FOR_BIDS

    override fun execute(node: JsonNode): Result<SetStateForBidsResult, Fail> {
        val params = node.tryGetParams(SetStateForBidsRequest::class.java, transform = transform)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return bidService.setStateForBids(params)
    }
}
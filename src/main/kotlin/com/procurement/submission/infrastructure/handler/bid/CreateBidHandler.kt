package com.procurement.submission.infrastructure.handler.bid

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.bid.create.CreateBidRequest
import com.procurement.submission.infrastructure.dto.bid.create.CreateBidResult
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractHistoricalHandler2
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateBidHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val bidService: BidService
) : AbstractHistoricalHandler2<Command2Type, CreateBidResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = CreateBidResult::class.java,
    transform = transform
) {
    override val action: Command2Type = Command2Type.CREATE_BID

    override fun execute(node: JsonNode): Result<CreateBidResult, Fail> {
        val params = node.tryGetParams(CreateBidRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it }

        return bidService.createBid(params)
    }
}
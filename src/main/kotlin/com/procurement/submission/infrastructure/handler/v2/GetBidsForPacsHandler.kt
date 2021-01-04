package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidQueryService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.GetBidsForPacsRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.GetBidsForPacsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class GetBidsForPacsHandler(
    logger: Logger,
    transform: Transform,
    private val bidQueryService: BidQueryService
) : AbstractHandlerV2<CommandTypeV2, GetBidsForPacsResult>(
    logger = logger,
    transform = transform
) {

    override val action: CommandTypeV2 = CommandTypeV2.GET_BIDS_FOR_PACS

    override fun execute(node: JsonNode): Result<GetBidsForPacsResult, Fail> {
        val params = node.tryGetParams(GetBidsForPacsRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it }

        return bidQueryService.getBidsForPacs(params)
    }
}
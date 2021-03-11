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
import com.procurement.submission.infrastructure.handler.v2.model.request.FindDocumentsByBidIdsRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.FindDocumentsByBidIdsResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class FindDocumentsByBidIdsHandler(
    logger: Logger,
    transform: Transform,
    private val bidQueryService: BidQueryService
) : AbstractHandlerV2<CommandTypeV2, FindDocumentsByBidIdsResult?>(
    logger = logger,
    transform = transform
) {

    override val action: CommandTypeV2 = CommandTypeV2.FIND_DOCUMENTS_BY_BID_IDS

    override fun execute(node: JsonNode): Result<FindDocumentsByBidIdsResult?, Fail> {
        val params = node.tryGetParams(FindDocumentsByBidIdsRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it }

        return bidQueryService.findDocumentByBidIds(params)
    }
}
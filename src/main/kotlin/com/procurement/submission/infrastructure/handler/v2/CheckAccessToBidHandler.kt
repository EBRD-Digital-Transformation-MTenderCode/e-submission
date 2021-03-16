package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckAccessToBidRequest
import com.procurement.submission.lib.functional.Validated
import org.springframework.stereotype.Component

@Component
class CheckAccessToBidHandler(
    val transform: Transform,
    logger: Logger,
    private val bidService: BidService
) : AbstractValidationHandlerV2<CommandTypeV2, Fail>(logger = logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CHECK_ACCESS_TO_BID

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node.tryGetParams(CheckAccessToBidRequest::class.java, transform = transform)
            .onFailure { return Validated.error(it.reason) }
            .convert()
            .onFailure { return Validated.error(it.reason) }

        return bidService.checkAccessToBid(params)
    }
}
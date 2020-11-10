package com.procurement.submission.infrastructure.handler.bid

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.bid.validate.ValidateBidDataRequest
import com.procurement.submission.infrastructure.handler.AbstractValidationHandlerV2
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asValidationError
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class ValidateBidDataHandler(
    logger: Logger,
    private val transform: Transform,
    private val bidService: BidService
) : AbstractValidationHandlerV2<CommandTypeV2, Fail>(
    logger = logger
) {
    override val action: CommandTypeV2 = CommandTypeV2.VALIDATE_BID_DATA

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node.tryGetParams(ValidateBidDataRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it.reason.asValidationError() }

        return bidService.validateBidData(params)
    }
}
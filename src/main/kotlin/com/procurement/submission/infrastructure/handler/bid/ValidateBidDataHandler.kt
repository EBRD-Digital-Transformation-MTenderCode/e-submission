package com.procurement.submission.infrastructure.handler.bid

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.bid.validate.ValidateBidDataRequest
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractValidationHandler2
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import com.procurement.submission.lib.functional.ValidationResult
import com.procurement.submission.lib.functional.asValidationFailure
import com.procurement.submission.lib.functional.bind
import org.springframework.stereotype.Component

@Component
class ValidateBidDataHandler(
    logger: Logger,
    private val transform: Transform,
    private val bidService: BidService
) : AbstractValidationHandler2<Command2Type, Fail>(
    logger = logger
) {
    override val action: Command2Type = Command2Type.VALIDATE_BID_DATA

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node.tryGetParams(ValidateBidDataRequest::class.java, transform = transform)
            .bind { it.convert() }
            .doReturn { error -> return error.asValidationFailure() }

        return bidService.validateBidData(params)
    }
}
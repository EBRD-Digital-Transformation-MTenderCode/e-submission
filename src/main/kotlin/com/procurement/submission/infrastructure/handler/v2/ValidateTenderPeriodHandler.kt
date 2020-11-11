package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.PeriodService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.ValidateTenderPeriodRequest
import com.procurement.submission.lib.functional.Validated
import com.procurement.submission.lib.functional.asValidationError
import org.springframework.stereotype.Component

@Component
class ValidateTenderPeriodHandler(
    logger: Logger,
    private val transform: Transform,
    private val periodService: PeriodService
) : AbstractValidationHandlerV2<CommandTypeV2, Fail>(
    logger = logger
) {
    override val action: CommandTypeV2 = CommandTypeV2.VALIDATE_TENDER_PERIOD

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node.tryGetParams(ValidateTenderPeriodRequest::class.java, transform = transform)
            .onFailure { return it.reason.asValidationError() }
            .convert()
            .onFailure { return it.reason.asValidationError() }

        return periodService.validateTenderPeriod(params)
    }
}
package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.PeriodService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.v1.model.request.CheckPeriodRequest
import com.procurement.submission.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.lib.functional.Validated
import org.springframework.stereotype.Component

@Component
class CheckPeriodHandler(
    val transform: Transform,
    logger: Logger,
    private val periodService: PeriodService
) : AbstractValidationHandlerV2<CommandTypeV2, Fail>(logger = logger) {

    override val action: CommandTypeV2 = CommandTypeV2.CHECK_PERIOD

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node.tryGetParams(CheckPeriodRequest::class.java, transform = transform)
            .onFailure { return Validated.error(it.reason) }
            .convert()
            .onFailure { return Validated.error(it.reason) }

        return periodService.checkPeriod(params)
    }
}
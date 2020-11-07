package com.procurement.submission.infrastructure.handler.tender.period

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.PeriodService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.tender.period.ValidateTenderPeriodRequest
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractValidationHandler2
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import com.procurement.submission.lib.functional.ValidationResult
import com.procurement.submission.lib.functional.asValidationFailure
import org.springframework.stereotype.Component

@Component
class ValidateTenderPeriodHandler(
    logger: Logger,
    private val transform: Transform,
    private val periodService: PeriodService
) : AbstractValidationHandler2<Command2Type, Fail>(
    logger = logger
) {
    override val action: Command2Type = Command2Type.VALIDATE_TENDER_PERIOD

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node.tryGetParams(ValidateTenderPeriodRequest::class.java, transform = transform)
            .doReturn { error -> return error.asValidationFailure() }
            .convert()
            .doReturn { error -> return error.asValidationFailure() }

        return periodService.validateTenderPeriod(params)
    }
}
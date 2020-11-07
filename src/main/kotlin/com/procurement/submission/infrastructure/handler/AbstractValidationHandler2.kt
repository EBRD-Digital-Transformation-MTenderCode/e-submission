package com.procurement.submission.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.web.api.response.ApiResponse2
import com.procurement.submission.infrastructure.web.api.response.ApiSuccessResponse2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import com.procurement.submission.lib.functional.ValidationResult

abstract class AbstractValidationHandler2<ACTION : Action, E : Fail>(
    private val logger: Logger
) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is ValidationResult.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiSuccessResponse2(version = version, id = id)
            }
            is ValidationResult.Fail -> generateResponseOnFailure(
                fail = result.error, version = version, id = id, logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): ValidationResult<E>
}

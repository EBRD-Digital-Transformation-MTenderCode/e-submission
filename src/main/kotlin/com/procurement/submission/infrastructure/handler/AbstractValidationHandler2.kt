package com.procurement.submission.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.web.api.response.ApiResponseV2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import com.procurement.submission.lib.functional.Validated

abstract class AbstractValidationHandler2<ACTION : Action, E : Fail>(
    private val logger: Logger
) : Handler<ACTION, ApiResponseV2> {

    override fun handle(node: JsonNode): ApiResponseV2 {
        val version = node.tryGetVersion()
            .onFailure {
                val id = node.tryGetId().getOrElse(CommandId.NaN)
                return generateResponseOnFailure(fail = it.reason, logger = logger, id = id)
            }

        val id = node.tryGetId()
            .onFailure {
                return generateResponseOnFailure(
                    fail = it.reason,
                    version = version,
                    id = CommandId.NaN,
                    logger = logger
                )
            }

        execute(node)
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }

        if (logger.isDebugEnabled)
            logger.debug("${action.key} has been executed.")
        return ApiResponseV2.Success(version = version, id = id)
    }

    abstract fun execute(node: JsonNode): Validated<E>
}

package com.procurement.submission.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.Action
import com.procurement.submission.infrastructure.api.CommandId
import com.procurement.submission.infrastructure.api.tryGetId
import com.procurement.submission.infrastructure.api.tryGetVersion
import com.procurement.submission.infrastructure.api.v2.ApiResponseV2
import com.procurement.submission.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.handler.Handler
import com.procurement.submission.lib.functional.Result

abstract class AbstractHandlerV2<ACTION : Action, R>(
    val transform: Transform,
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
            .let { result ->
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: '${transform.trySerialization(result)}'")
                return ApiResponseV2.Success(version = version, id = id, result = result)
            }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}
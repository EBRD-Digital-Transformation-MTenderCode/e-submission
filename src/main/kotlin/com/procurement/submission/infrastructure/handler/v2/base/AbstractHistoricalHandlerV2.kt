package com.procurement.submission.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.extension.nowDefaultUTC
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.Action
import com.procurement.submission.infrastructure.api.CommandId
import com.procurement.submission.infrastructure.api.tryGetAction
import com.procurement.submission.infrastructure.api.tryGetId
import com.procurement.submission.infrastructure.api.tryGetVersion
import com.procurement.submission.infrastructure.api.v2.ApiResponseV2
import com.procurement.submission.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.handler.Handler
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.utils.toJson

abstract class AbstractHistoricalHandlerV2<ACTION : Action, R>(
    private val target: Class<R>,
    private val historyRepository: HistoryRepository,
    val transform: Transform,
    private val logger: Logger
) : Handler<ACTION, ApiResponseV2> {

    override fun handle(node: JsonNode): ApiResponseV2 {
        val version = node.tryGetVersion()
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, id = CommandId.NaN, logger = logger)
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

        val action = node.tryGetAction()
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }

        val history = historyRepository.getHistory(id, action)
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }

        if (history != null) {
            val result = transform.tryDeserialization(value = history, target = target)
                .onFailure { incident ->
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Database.Parsing(
                            column = "json_data",
                            value = history,
                            exception = incident.reason.exception
                        ),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }
            return ApiResponseV2.Success(version = version, id = id, result = result)
        }

        return execute(node)
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }
            .let { result ->
                if (result != null) {
                    val historyEntity = HistoryEntity(
                        commandId = id,
                        action = action,
                        date = nowDefaultUTC(),
                        data = toJson(result)
                    )
                    historyRepository.saveHistory(historyEntity)
                }
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: '${transform.trySerialization(result)}'")

                ApiResponseV2.Success(version = version, id = id, result = result)
            }
    }

    abstract fun execute(node: JsonNode): Result<R?, Fail>
}


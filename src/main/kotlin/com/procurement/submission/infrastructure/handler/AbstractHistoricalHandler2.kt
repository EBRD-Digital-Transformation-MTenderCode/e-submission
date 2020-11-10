package com.procurement.submission.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.extension.nowDefaultUTC
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.infrastructure.web.api.response.ApiResponse2
import com.procurement.submission.infrastructure.web.api.response.ApiSuccessResponse2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.response.parser.tryGetAction
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.utils.toJson

abstract class AbstractHistoricalHandler2<ACTION : Action, R>(
    private val target: Class<R>,
    private val historyRepository: HistoryRepository,
    val transform: Transform,
    private val logger: Logger
) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
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
            return ApiSuccessResponse2(version = version, id = id, result = result)
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

                ApiSuccessResponse2(version = version, id = id, result = result)
            }
    }

    abstract fun execute(node: JsonNode): Result<R?, Fail>
}


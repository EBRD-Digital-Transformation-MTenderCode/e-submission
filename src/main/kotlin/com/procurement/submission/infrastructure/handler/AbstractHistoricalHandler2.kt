package com.procurement.submission.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.repository.HistoryRepository
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.repository.HistoryRepositoryCassandra
import com.procurement.submission.infrastructure.web.api.response.ApiResponse2
import com.procurement.submission.infrastructure.web.api.response.ApiSuccessResponse2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import com.procurement.submission.lib.functional.Result
import java.util.*

abstract class AbstractHistoricalHandler2<ACTION : Action, R>(
    private val target: Class<R>,
    private val historyRepository: HistoryRepository,
    val transform: Transform,
    private val logger: Logger
) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
        val version = node.tryGetVersion()
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, id = UUID(0, 0), logger = logger)
            }
        val id = node.tryGetId()
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = UUID(0, 0), logger = logger)
            }

        val history = historyRepository.getHistory(id.toString(), action.key)
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }

        if (history != null) {
            val data = history.jsonData
            val result = transform.tryDeserialization(value = data, target = target)
                .onFailure { incident ->
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Database.Parsing(
                            column = HistoryRepositoryCassandra.JSON_DATA,
                            value = data,
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
                if (result != null)
                    historyRepository.saveHistory(id.toString(), action.key, result)
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: '${transform.trySerialization(result)}'")

                ApiSuccessResponse2(version = version, id = id, result = result)
            }
    }

    abstract fun execute(node: JsonNode): Result<R?, Fail>
}


package com.procurement.submission.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.repository.HistoryRepository
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.infrastructure.repository.HistoryRepositoryCassandra
import com.procurement.submission.infrastructure.web.api.response.ApiResponse2
import com.procurement.submission.infrastructure.web.api.response.ApiSuccessResponse2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion

abstract class AbstractHistoricalHandler2<ACTION : Action, R>(
    private val target: Class<R>,
    private val historyRepository: HistoryRepository,
    val transform: Transform,
    private val logger: Logger
) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        val history = historyRepository.getHistory(id.toString(), action.key)
            .doOnError { error ->
                return generateResponseOnFailure(
                    fail = error, version = version, id = id, logger = logger
                )
            }
            .get
        if (history != null) {
            val data = history.jsonData
            val result = transform.tryDeserialization(value = data, target = target)
                .doReturn { incident ->
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Database.Parsing(
                            column = HistoryRepositoryCassandra.JSON_DATA, value = data, exception = incident.exception
                        ),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }
            return ApiSuccessResponse2(version = version, id = id, result = result)
        }

        return when (val result = execute(node)) {
            is Result.Success -> {
                val resultData = result.get
                if (resultData != null)
                    historyRepository.saveHistory(id.toString(), action.key, resultData)
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: '${transform.trySerialization(result.get)}'")

                ApiSuccessResponse2(version = version, id = id, result = resultData)
            }
            is Result.Failure -> generateResponseOnFailure(
                fail = result.error, version = version, id = id, logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): Result<R?, Fail>
}


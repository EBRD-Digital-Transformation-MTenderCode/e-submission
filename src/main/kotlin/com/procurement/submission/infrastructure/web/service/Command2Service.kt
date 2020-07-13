package com.procurement.submission.infrastructure.web.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.application.service.Logger
import com.procurement.submission.infrastructure.web.response.parser.tryGetAction
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import com.procurement.submission.infrastructure.web.api.response.ApiResponse2
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val logger: Logger
) {

    fun execute(node: JsonNode): ApiResponse2 {

        val version = node.tryGetVersion()
            .doReturn { versionFail ->
                val id = node.tryGetId()
                    .doReturn { idFail -> return generateResponseOnFailure(fail = idFail, logger = logger) }
                return generateResponseOnFailure(fail = versionFail, logger = logger, id = id)
            }

        val id = node.tryGetId()
            .doReturn { fail ->
                return generateResponseOnFailure(fail = fail, version = version, logger = logger)
            }

        val action = node.tryGetAction()
            .doReturn { error ->
                return generateResponseOnFailure(fail = error, id = id, version = version, logger = logger)
            }

        return TODO()
    }
}
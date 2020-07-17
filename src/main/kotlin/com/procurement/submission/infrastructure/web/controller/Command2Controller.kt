package com.procurement.submission.infrastructure.web.controller

import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.functional.Result
import com.procurement.submission.infrastructure.configuration.properties.GlobalProperties2
import com.procurement.submission.infrastructure.service.Command2Service
import com.procurement.submission.infrastructure.web.api.response.ApiResponse2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.api.version.ApiVersion2
import com.procurement.submission.infrastructure.web.response.parser.NaN
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetNode
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/command2")
class Command2Controller(
    private val command2Service: Command2Service,
    private val transform: Transform,
    private val logger: Logger
) {

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse2> {
        if (logger.isDebugEnabled)
            logger.debug("RECEIVED COMMAND: '$requestBody'.")

        val node = requestBody.tryGetNode(transform)
            .doReturn { error -> return generateResponseEntityOnFailure(fail = error) }

        val version = when (val versionResult = node.tryGetVersion()) {
            is Result.Success -> versionResult.get
            is Result.Failure -> {
                return when (val idResult = node.tryGetId()) {
                    is Result.Success -> generateResponseEntityOnFailure(
                        fail = versionResult.error,
                        id = idResult.get
                    )
                    is Result.Failure -> generateResponseEntityOnFailure(fail = versionResult.error)
                }
            }
        }

        val id = node.tryGetId()
            .doReturn { error -> return generateResponseEntityOnFailure(fail = error, version = version) }

        val response =
            command2Service.execute(node)
                .also { response ->
                    if (logger.isDebugEnabled)
                        logger.debug("RESPONSE (id: '${id}'): '${transform.trySerialization(response)}'.")
                }

        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun generateResponseEntityOnFailure(
        fail: Fail, version: ApiVersion2 = GlobalProperties2.App.apiVersion, id: UUID = NaN
    ): ResponseEntity<ApiResponse2> {
        val response = generateResponseOnFailure(
            fail = fail, id = id, version = version, logger = logger
        )
        return ResponseEntity(response, HttpStatus.OK)
    }
}
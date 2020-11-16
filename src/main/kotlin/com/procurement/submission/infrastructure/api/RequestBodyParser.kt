package com.procurement.submission.infrastructure.api

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.BadRequest
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.extension.jackson.tryGetAttribute
import com.procurement.submission.infrastructure.extension.jackson.tryGetAttributeAsEnum
import com.procurement.submission.infrastructure.extension.jackson.tryGetTextAttribute
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.flatMap

fun JsonNode.tryGetVersion(): Result<ApiVersion, DataErrors> {
    val name = "version"
    return tryGetTextAttribute(name)
        .flatMap { version ->
            ApiVersion.orNull(version)
                ?.asSuccess<ApiVersion, DataErrors>()
                ?: DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    expectedFormat = ApiVersion.pattern,
                    actualValue = version
                ).asFailure()
        }
}

fun JsonNode.tryGetAction(): Result<CommandTypeV2, DataErrors> =
    tryGetAttributeAsEnum("action", CommandTypeV2)

fun <T : Any> JsonNode.tryGetParams(target: Class<T>, transform: Transform): Result<T, Fail.Error> {
    val name = "params"
    return tryGetAttribute(name).flatMap {
        when (val result = transform.tryMapping(it, target)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                BadRequest("Error parsing '$name'")
            )
        }
    }
}

fun <T : Any> JsonNode.tryGetData(target: Class<T>, transform: Transform): Result<T, Fail.Error> =
    when (val result = transform.tryMapping(this, target)) {
        is Result.Success -> result
        is Result.Failure -> Result.failure(
            BadRequest("Error parsing 'data'")
        )
    }

fun JsonNode.tryGetId(): Result<CommandId, DataErrors> = tryGetTextAttribute("id").map { CommandId(it) }

fun String.tryGetNode(transform: Transform): Result<JsonNode, BadRequest> =
    when (val result = transform.tryParse(this)) {
        is Result.Success -> result
        is Result.Failure -> Result.failure(BadRequest())
    }

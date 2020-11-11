package com.procurement.submission.infrastructure.api.v2

import com.procurement.submission.application.service.Logger
import com.procurement.submission.domain.extension.nowDefaultUTC
import com.procurement.submission.domain.extension.toListOrEmpty
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.fail.error.ValidationError
import com.procurement.submission.infrastructure.api.ApiVersion
import com.procurement.submission.infrastructure.api.CommandId
import com.procurement.submission.infrastructure.configuration.properties.GlobalProperties2

object ApiResponseV2Generator {

    fun generateResponseOnFailure(
        fail: Fail,
        version: ApiVersion = GlobalProperties2.App.apiVersion,
        id: CommandId,
        logger: Logger
    ): ApiResponseV2 {
        fail.logging(logger)
        return when (fail) {
            is Fail.Error -> {
                when (fail) {
                    is DataErrors.Validation -> generateDataErrorResponse(id = id, version = version, dataError = fail)
                    is ValidationError -> generateValidationErrorResponse(
                        id = id, version = version, validationError = fail
                    )
                    else -> generateErrorResponse(id = id, version = version, error = fail)
                }
            }
            is Fail.Incident -> generateIncidentResponse(id = id, version = version, incident = fail)
        }
    }

    private fun generateDataErrorResponse(dataError: DataErrors.Validation, version: ApiVersion, id: CommandId) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Result(
                    code = getFullErrorCode(dataError.code),
                    description = dataError.description,
                    details = ApiResponseV2.Error.Result.Detail.tryCreateOrNull(
                        name = dataError.name
                    ).toListOrEmpty()
                )
            )
        )

    private fun generateValidationErrorResponse(validationError: ValidationError, version: ApiVersion, id: CommandId) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Result(
                    code = getFullErrorCode(validationError.code),
                    description = validationError.description,
                    details = ApiResponseV2.Error.Result.Detail.tryCreateOrNull(
                        id = validationError.entityId
                    ).toListOrEmpty()
                )
            )
        )

    private fun generateErrorResponse(version: ApiVersion, id: CommandId, error: Fail.Error) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Result(
                    code = getFullErrorCode(error.code),
                    description = error.description
                )
            )
        )

    private fun generateIncidentResponse(incident: Fail.Incident, version: ApiVersion, id: CommandId) =
        ApiResponseV2.Incident(
            version = version,
            id = id,
            result = ApiResponseV2.Incident.Result(
                date = nowDefaultUTC(),
                id = IncidentId.generate(),
                level = incident.level,
                service = ApiResponseV2.Incident.Result.Service(
                    id = GlobalProperties2.service.id,
                    version = GlobalProperties2.service.version,
                    name = GlobalProperties2.service.name
                ),
                details = listOf(
                    ApiResponseV2.Incident.Result.Detail(
                        code = getFullErrorCode(incident.code),
                        description = incident.description,
                        metadata = null
                    )
                )
            )
        )

    private fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties2.service.id}"
}
package com.procurement.submission.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.exception.EnumException
import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.domain.extension.parseLocalDateTime
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.domain.model.tryOwner
import com.procurement.submission.infrastructure.api.Action
import com.procurement.submission.infrastructure.api.ApiVersion
import com.procurement.submission.infrastructure.api.CommandId
import java.time.LocalDateTime
import java.util.*

data class CommandMessage @JsonCreator constructor(
    val id: CommandId,
    val command: CommandTypeV1,
    val context: Context,
    val data: JsonNode,
    val version: ApiVersion
)

data class Context @JsonCreator constructor(
    val operationId: String?,
    val requestId: String?,
    val cpid: String?,
    val ocid: String?,
    val stage: String?,
    val prevStage: String?,
    val processType: String?,
    val operationType: String?,
    val phase: String?,
    val owner: String?,
    val country: String?,
    val language: String?,
    val pmd: String?,
    val token: String?,
    val access: String?,
    val startDate: String?,
    val endDate: String?,
    val id: String?,
    val awardCriteria: String?

)

val CommandMessage.commandId: CommandId
    get() = this.id

val CommandMessage.action: Action
    get() = this.command

val CommandMessage.token: UUID
    get() = this.context.token
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

val CommandMessage.cpid: Cpid
    get() = this.context.cpid
        ?.let {
            Cpid.tryCreateOrNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE,
                    message = "Cannot parse 'cpid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

val CommandMessage.ocid: Ocid
    get() = this.context.ocid
        ?.let {
            Ocid.tryCreateOrNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE,
                    message = "Cannot parse 'ocid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

val CommandMessage.ctxId: String
    get() = this.context.id
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

val CommandMessage.pmd: ProcurementMethod
    get() = this.context.pmd
        ?.let { ProcurementMethod.fromString(it) }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'pmd' attribute in context.")

val CommandMessage.owner: Owner
    get() = this.context.owner
        ?.let { value ->
            value.tryOwner()
                .onFailure {
                    throw ErrorException(
                        error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE,
                        message = "Cannot parse 'owner' attribute '${value}'."
                    )
                }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

val CommandMessage.phase: String
    get() = this.context.phase
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'phase' attribute in context.")

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate
        ?.parseLocalDateTime()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

val CommandMessage.endDate: LocalDateTime
    get() = this.context.endDate
        ?.parseLocalDateTime()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'endDate' attribute in context.")

val CommandMessage.country: String
    get() = this.context.country
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'country' attribute in context.")

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDto(
    val id: String? = null,
    val errors: List<ResponseErrorDto>? = null,
    val data: Any? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseErrorDto(
    val code: String,
    val description: String?
)

fun getExceptionResponseDto(exception: Exception) = ResponseDto(
    errors = listOf(
        ResponseErrorDto(code = "400.04.00", description = exception.message)
    )
)

fun getErrorExceptionResponseDto(error: ErrorException, id: String? = null) = ResponseDto(
    errors = listOf(
        ResponseErrorDto(code = "400.04." + error.code, description = error.msg)
    ),
    id = id
)

fun getEnumExceptionResponseDto(error: EnumException, id: String? = null) = ResponseDto(
    errors = listOf(
        ResponseErrorDto(code = "400.04." + error.code, description = error.msg)
    ),
    id = id
)

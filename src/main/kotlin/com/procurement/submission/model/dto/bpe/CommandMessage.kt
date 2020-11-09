package com.procurement.submission.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.exception.EnumException
import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.extension.parseLocalDateTime
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.model.CommandId
import java.time.LocalDateTime
import java.util.*

data class CommandMessage @JsonCreator constructor(

        val id: String,
        val command: CommandType,
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
    get() = CommandId(this.id)

val CommandMessage.action: Action
    get() = this.command

val CommandMessage.token: UUID
    get() = this.context.token?.let { id ->
        try {
            UUID.fromString(id)
        } catch (exception: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
        }
    } ?: throw ErrorException(
        error = ErrorType.CONTEXT,
        message = "Missing the 'token' attribute in context."
    )

val CommandMessage.cpid: Cpid
    get() = this.context.cpid
        ?.let {
            Cpid.tryCreateOrNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE,
                    message = "Cannot parse 'cpid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'cpid' attribute in context."
        )

val CommandMessage.ocid: Ocid
    get() = this.context.ocid
        ?.let {
            Ocid.tryCreateOrNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_FORMAT_OF_ATTRIBUTE,
                    message = "Cannot parse 'ocid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'ocid' attribute in context."
        )

val CommandMessage.ctxId: String
    get() = this.context.id
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'id' attribute in context."
        )

val CommandMessage.stage: String
    get() = this.context.stage
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'stage' attribute in context."
        )

val CommandMessage.prevStage: String
    get() = this.context.prevStage
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'prevStage' attribute in context."
        )

val CommandMessage.pmd: ProcurementMethod
    get() = this.context.pmd?.let {
        ProcurementMethod.fromString(it)
    } ?: throw ErrorException(
        error = ErrorType.CONTEXT,
        message = "Missing the 'pmd' attribute in context."
    )

val CommandMessage.owner: String
    get() = this.context.owner
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'owner' attribute in context."
        )

val CommandMessage.phase: String
    get() = this.context.phase
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'phase' attribute in context."
        )

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate?.parseLocalDateTime()
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'startDate' attribute in context."
        )

val CommandMessage.endDate: LocalDateTime
    get() = this.context.endDate?.parseLocalDateTime()
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'endDate' attribute in context."
        )

val CommandMessage.country: String
    get() = this.context.country
        ?: throw ErrorException(
            error = ErrorType.CONTEXT,
            message = "Missing the 'country' attribute in context."
        )

enum class CommandType(override val key: String) : Action {
    APPLY_EVALUATED_AWARDS("applyAwardingRes"),
    BID_WITHDRAWN("bidWithdrawn"),
    CHECK_PERIOD("checkPeriod"),
    CHECK_PERIOD_END_DATE("checkPeriodEndDate"),
    CHECK_TOKEN_OWNER("checkTokenOwner"),
    CREATE_BID("createBid"),
    FINAL_BIDS_STATUS_BY_LOTS("finalBidsStatusByLots"),
    GET_BIDS_AUCTION("getBidsAuction"),
    GET_BIDS_BY_LOTS("getBidsByLots"),
    GET_BIDS_FOR_EVALUATION("getBidsForEvaluation"),
    GET_DOCS_OF_CONSIDERED_BID("getDocsOfConsideredBid"),
    OPEN_BID_DOCS("openBidDocs"),
    OPEN_BIDS_FOR_PUBLISHING("openBidsForPublishing"),
    SAVE_NEW_PERIOD("saveNewPeriod"),
    SAVE_PERIOD("savePeriod"),
    UPDATE_BID("updateBid"),
    UPDATE_BID_BY_AWARD_STATUS("updateBidBAwardStatus"),
    UPDATE_BID_DOCS("updateBidDocs"),
    VALIDATE_PERIOD("validatePeriod"),
    ;

    @JsonValue
    fun value(): String = this.key

    override fun toString(): String {
        return this.key
    }
}

enum class ApiVersion(private val value: String) {
    V_0_0_1("0.0.1");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDto(

        val errors: List<ResponseErrorDto>? = null,

        val data: Any? = null,

        val id: String? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseErrorDto(

        val code: String,

        val description: String?
)

fun getExceptionResponseDto(exception: Exception): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.04.00",
                    description = exception.message
            )))
}

fun getErrorExceptionResponseDto(error: ErrorException, id: String? = null): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.04." + error.code,
                    description = error.msg
            )),
            id = id)
}

fun getEnumExceptionResponseDto(error: EnumException, id: String? = null): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.04." + error.code,
                    description = error.msg
            )),
            id = id)
}


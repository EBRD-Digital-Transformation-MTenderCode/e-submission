package com.procurement.submission.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.exception.EnumException
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType

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

val CommandMessage.cpid: String
    get() = this.context.cpid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

val CommandMessage.stage: String
    get() = this.context.stage
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'stage' attribute in context.")

enum class CommandType(private val value: String) {
    CREATE_BID("createBid"),
    UPDATE_BID("updateBid"),
    UPDATE_BID_DOCS("updateBidDocs"),
    COPY_BIDS("copyBids"),
    GET_PERIOD("getPeriod"),
    SAVE_PERIOD("savePeriod"),
    SAVE_NEW_PERIOD("saveNewPeriod"),
    VALIDATE_PERIOD("validatePeriod"),
    CHECK_PERIOD_END_DATE("checkPeriodEndDate"),
    CHECK_PERIOD("checkPeriod"),
    CHECK_TOKEN_OWNER("checkTokenOwner"),
    GET_BIDS("getBids"),
    GET_BIDS_AUCTION("getBidsAuction"),
    UPDATE_BIDS_BY_LOTS("updateBidsByLots"),
    UPDATE_BID_BY_AWARD_STATUS("updateBidBAwardStatus"),
    SET_BIDS_FINAL_STATUSES("setBidsFinalStatuses"),
    BID_WITHDRAWN("bidWithdrawn"),
    PREPARE_BIDS_CANCELLATION("prepareBidsCancellation"),
    BIDS_CANCELLATION("bidsCancellation"),
    GET_DOCS_OF_CONSIDERED_BID("getDocsOfConsideredBid"),
    SET_INITIAL_BIDS_STATUS("setInitialBidsStatus"),
    APPLY_EVALUATED_AWARDS("applyAwardingRes");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
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


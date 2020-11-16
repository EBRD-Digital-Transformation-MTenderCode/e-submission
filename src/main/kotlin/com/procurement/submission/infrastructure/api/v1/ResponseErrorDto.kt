package com.procurement.submission.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.application.exception.EnumException
import com.procurement.submission.application.exception.ErrorException

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

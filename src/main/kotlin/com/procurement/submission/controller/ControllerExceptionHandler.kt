package com.procurement.submission.controller

import com.procurement.submission.exception.EnumException
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.model.dto.bpe.ResponseDetailsDto
import com.procurement.submission.model.dto.bpe.ResponseDto
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        return when (ex) {
            is ErrorException -> ResponseDto(false, getErrors(ex.code, ex.msg), null)
            is EnumException -> ResponseDto(false, getErrors(ex.code, ex.msg), null)
            else -> ResponseDto(false, getErrors("Exception", ex.message), null)
        }
    }

    private fun getErrors(code: String, error: String?) =
            listOf(ResponseDetailsDto(code = "400.04.$code", message = error!!))
}

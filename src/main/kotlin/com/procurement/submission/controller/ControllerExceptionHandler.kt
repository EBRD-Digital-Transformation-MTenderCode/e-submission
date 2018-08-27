package com.procurement.submission.controller

import com.procurement.submission.exception.EnumException
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.bpe.getEnumExceptionResponseDto
import com.procurement.submission.model.dto.bpe.getErrorExceptionResponseDto
import com.procurement.submission.model.dto.bpe.getExceptionResponseDto
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
            is ErrorException -> getErrorExceptionResponseDto(ex)
            is EnumException -> getEnumExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }
}

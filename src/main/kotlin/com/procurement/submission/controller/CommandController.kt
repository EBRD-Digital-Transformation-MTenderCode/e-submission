package com.procurement.submission.controller

import com.procurement.submission.exception.EnumException
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.model.dto.bpe.*
import com.procurement.submission.service.CommandService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/command")
class CommandController(private val commandService: CommandService) {

    @PostMapping
    fun command(@RequestBody cm: CommandMessage): ResponseEntity<ResponseDto> {
        return ResponseEntity(commandService.execute(cm), HttpStatus.OK)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        return when (ex) {
            is ErrorException -> getErrorExceptionResponseDto(ex)
            is EnumException -> getEnumExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }
}




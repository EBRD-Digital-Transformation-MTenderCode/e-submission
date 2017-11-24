package com.procurement.submission.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.response.ErrorInsertResponse;
import com.procurement.submission.model.dto.response.MappingErrorResponse;
import com.procurement.submission.model.dto.response.ValidationErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {
    private static final String MESSAGE = "Houston we have a problem";

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ValidationErrorResponse handleValidationContractProcessPeriod(
        final ValidationException e) {
        return new ValidationErrorResponse(
            MESSAGE,
            e.getErrors().getFieldErrors().stream()
             .map(f -> new ValidationErrorResponse.ErrorPoint(
                 f.getField(),
                 f.getDefaultMessage(),
                 f.getCode()))
             .collect(Collectors.toList()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonMappingException.class)
    public MappingErrorResponse handleJsonMappingExceptionException(final JsonMappingException e) {
        return new MappingErrorResponse(MESSAGE, e);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ErrorException.class)
    public ErrorInsertResponse handleErrorInsertException(final ErrorException e) {
        return new ErrorInsertResponse(e.getMessage());
    }
}

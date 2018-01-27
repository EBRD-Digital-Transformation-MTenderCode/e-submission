package com.procurement.submission.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.response.errors.ErrorInsertResponse;
import com.procurement.submission.model.dto.response.errors.MappingErrorResponse;
import com.procurement.submission.model.dto.response.errors.ValidationErrorResponse;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ControllerExceptionHandler {
    private static final String MESSAGE = "Something went wrong";

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationContractProcessPeriod(
        final ValidationException e) {
        return new ResponseEntity<>(getValidationErrorResponse(e.getErrors()), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> methodArgumentNotValidException(
        final MethodArgumentNotValidException e) {
        return new ResponseEntity<>(getValidationErrorResponse(e.getBindingResult()), BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handle(
        final ConstraintViolationException e) {
        return new ResponseEntity<>(getValidationErrorResponse(e), BAD_REQUEST);
    }

    private ValidationErrorResponse getValidationErrorResponse(final ConstraintViolationException e) {
        return new ValidationErrorResponse(
            MESSAGE,
            e.getConstraintViolations()
             .stream()
             .map(s -> new ValidationErrorResponse.ErrorPoint(
                 s.getPropertyPath().toString(),
                 s.getMessage(),
                 s.getMessageTemplate()))
             .collect(toList()));
    }

    private ValidationErrorResponse getValidationErrorResponse(final BindingResult e) {
        return new ValidationErrorResponse(
            MESSAGE,
            e.getFieldErrors()
             .stream()
             .map(getErrorPointFunction())
             .collect(toList()));
    }

    private Function<FieldError, ValidationErrorResponse.ErrorPoint> getErrorPointFunction() {
        return f -> new ValidationErrorResponse.ErrorPoint(
            f.getField(),
            f.getDefaultMessage(),
            f.getCode());
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(JsonMappingException.class)
    public MappingErrorResponse handleJsonMappingExceptionException(final JsonMappingException e) {
        return new MappingErrorResponse(MESSAGE, e);
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ErrorException.class)
    public ErrorInsertResponse handleErrorInsertException(final ErrorException e) {
        return new ErrorInsertResponse(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ServletException.class)
    public ErrorInsertResponse handleErrorInsertException(final ServletException e) {
        return new ErrorInsertResponse(e.getMessage());
    }
}

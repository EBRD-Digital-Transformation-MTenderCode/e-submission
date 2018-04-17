package com.procurement.submission.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.procurement.submission.model.dto.bpe.ResponseDetailsDto;
import com.procurement.submission.model.dto.bpe.ResponseDto;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final String ERROR_PREFIX = "400.04.";

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ResponseDto<>(false, getErrors(e.getBindingResult()), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDto handle(final ConstraintViolationException e) {
        return new ResponseDto<>(false, getErrors(e), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(JsonMappingException.class)
    public ResponseDto handleJsonMappingExceptionException(final JsonMappingException e) {
        return new ResponseDto<>(false, getErrors(e.getClass().getName(), e.getMessage()), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseDto handleJsonMappingExceptionException(final IllegalArgumentException e) {
        return new ResponseDto<>(false, getErrors(e.getClass().getName(), e.getMessage()), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseDto handleJsonMappingExceptionException(final MethodArgumentTypeMismatchException e) {
        return new ResponseDto<>(false, getErrors(e.getClass().getName(), e.getMessage()), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(ServletException.class)
    public ResponseDto handleErrorInsertException(final ServletException e) {
        return new ResponseDto<>(false, getErrors(e.getClass().getName(), e.getMessage()), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(EnumException.class)
    public ResponseDto handleErrorInsertException(final EnumException e) {
        return new ResponseDto<>(false, getErrors(e.getCode(), e.getMessage()), null);
    }

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(ErrorException.class)
    public ResponseDto handleErrorInsertException(final ErrorException e) {
        return new ResponseDto<>(false, getErrors(e.getCode(), e.getMessage()), null);
    }

    private List<ResponseDetailsDto> getErrors(final String code, final String error) {
        return Collections.singletonList(new ResponseDetailsDto(ERROR_PREFIX + code, error));
    }

    private List<ResponseDetailsDto> getErrors(final BindingResult result) {
        return result.getFieldErrors()
                .stream()
                .map(f -> new ResponseDetailsDto(
                        ERROR_PREFIX + f.getField(),
                        f.getCode() + " : " + f.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    private List<ResponseDetailsDto> getErrors(final ConstraintViolationException e) {
        return e.getConstraintViolations()
                .stream()
                .map(f -> new ResponseDetailsDto(
                        ERROR_PREFIX + f.getPropertyPath().toString(),
                        f.getMessage() + " " + f.getMessageTemplate()))
                .collect(toList());
    }
}

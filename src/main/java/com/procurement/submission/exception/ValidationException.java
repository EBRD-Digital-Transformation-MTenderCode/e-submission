package com.procurement.submission.exception;

import java.util.List;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Getter
public class ValidationException extends RuntimeException {
    final BindingResult errors;

    public ValidationException(final BindingResult errors) {
        this.errors = errors;
    }
}

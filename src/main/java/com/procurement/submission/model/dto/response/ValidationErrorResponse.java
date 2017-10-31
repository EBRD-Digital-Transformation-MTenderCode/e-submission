package com.procurement.submission.model.dto.response;

import java.util.List;
import lombok.Getter;
import org.springframework.validation.ObjectError;

@Getter
public class ValidationErrorResponse {
    private String message;
    private List<ErrorPoint> errors;

    public ValidationErrorResponse(String message, List<ErrorPoint> errors) {
        this.message = message;
        this.errors = errors;
    }

    @Getter
    public static class ErrorPoint {
        private String field;
        private String message;
        private String code;

        public ErrorPoint(String field, String message, String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }
    }
}

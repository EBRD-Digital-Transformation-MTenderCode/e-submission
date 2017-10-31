package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;

@Getter
public class MappingErrorResponse {
    final String message;
    final JsonMappingException e;

    public MappingErrorResponse(String message, JsonMappingException e) {
        this.message = message;
        this.e = e;
    }
}

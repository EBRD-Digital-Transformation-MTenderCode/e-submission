package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;

@Getter
public class MappingErrorResponse {
    private String message;
    private JsonMappingException e;

    public MappingErrorResponse(final String message, final JsonMappingException e) {
        this.message = message;
        this.e = e;
    }
}

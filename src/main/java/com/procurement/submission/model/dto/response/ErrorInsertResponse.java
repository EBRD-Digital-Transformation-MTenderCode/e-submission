package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorInsertResponse {

    @JsonProperty("message")
    private String message;

    public ErrorInsertResponse(@JsonProperty("message") final String message) {
        this.message = message;
    }
}

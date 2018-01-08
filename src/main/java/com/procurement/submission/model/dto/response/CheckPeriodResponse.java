package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CheckPeriodResponse {

    @JsonProperty("period")
    private final boolean periodIsValid;

    @JsonCreator
    public CheckPeriodResponse(@JsonProperty("period") final boolean periodIsValid) {
        this.periodIsValid = periodIsValid;
    }
}
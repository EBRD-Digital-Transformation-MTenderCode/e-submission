package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "periodValid",
        "periodChange"
})
public class CheckPeriodResponseDto {

    @JsonProperty("periodValid")
    private final Boolean periodValid;

    @JsonProperty("periodChange")
    private final Boolean periodChange;

    @JsonCreator
    public CheckPeriodResponseDto(@JsonProperty("isPeriodValid") final Boolean periodValid,
                                  @JsonProperty("isPeriodChange") final Boolean periodChange) {
        this.periodValid = periodValid;
        this.periodChange = periodChange;
    }
}
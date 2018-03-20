package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "isPeriodValid",
        "isPeriodChanged"
})
public class CheckPeriodResponseDto {

    @JsonProperty("isPeriodValid")
    private final Boolean isPeriodValid;

    @JsonProperty("isPeriodChanged")
    private final Boolean isPeriodChanged;

    @JsonCreator
    public CheckPeriodResponseDto(@JsonProperty("isPeriodValid") final Boolean isPeriodValid,
                                  @JsonProperty("isPeriodChanged") final Boolean isPeriodChanged) {
        this.isPeriodValid = isPeriodValid;
        this.isPeriodChanged = isPeriodChanged;
    }
}

package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "tenderId",
        "startDate",
        "endDate"
})
public class PeriodResponseDto {

    @JsonProperty("tenderId")
    private final String tenderId;

    @JsonProperty("startDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime startDate;

    @JsonProperty("endDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime endDate;

    @JsonCreator
    public PeriodResponseDto(@JsonProperty("tenderId") final String tenderId,
                             @JsonProperty("startDate") final LocalDateTime startDate,
                             @JsonProperty("endDate") final LocalDateTime endDate) {
        this.tenderId = tenderId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
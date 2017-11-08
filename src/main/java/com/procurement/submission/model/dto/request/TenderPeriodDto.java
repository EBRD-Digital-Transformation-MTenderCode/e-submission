package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TenderPeriodDto {

    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    @JsonCreator
    public TenderPeriodDto(@JsonProperty("startDate") final LocalDateTime startDate,
                           @JsonProperty("endDate") final LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

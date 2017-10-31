package com.procurement.submission.model.dto;

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
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@ToString
public class ContractProcessPeriodDto {

    @NotNull
    private String id;

    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDateTime;

    @NotNull
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime finishDateTime;

    @JsonCreator
    public ContractProcessPeriodDto(@JsonProperty("id") final String id,
                                    @JsonProperty("startDateTime") final LocalDateTime startDateTime,
                                    @JsonProperty("finishDateTime") final LocalDateTime finishDateTime) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
    }
}

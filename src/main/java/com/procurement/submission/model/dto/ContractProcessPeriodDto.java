package com.procurement.submission.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ContractProcessPeriodDto {

    @NotNull
    private String id;

    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDate startDateTime;

    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDate finishDateTime;

    public ContractProcessPeriodDto(@NotNull @JsonProperty("id") String id,
                                    @NotNull @JsonProperty("startDateTime") LocalDate startDateTime,
                                    @NotNull @JsonProperty("finishDateTime") LocalDate finishDateTime) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.finishDateTime = finishDateTime;
    }
}

package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class QualificationOfferResponseDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("createdDate")
    private LocalDateTime createdDate;

    @JsonCreator
    public QualificationOfferResponseDto(@JsonProperty("id") final String id,
                                         @JsonProperty("createdDate") final LocalDateTime createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }
}

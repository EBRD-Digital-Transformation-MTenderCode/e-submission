package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class BidsUpdateStatusDetailsResponse {

    @JsonProperty("bid")
    private BidUpdate bid;

    @JsonCreator
    public BidsUpdateStatusDetailsResponse(@JsonProperty("bid") final BidUpdate bid) {
        this.bid = bid;
    }
}

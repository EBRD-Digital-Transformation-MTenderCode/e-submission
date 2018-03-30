package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.submission.model.ocds.Bid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BidRequestDto {

    @NotNull
    @JsonProperty("bid")
    private Bid bid;

    @JsonCreator
    public BidRequestDto(@JsonProperty("bid") final Bid bid) {
        this.bid = bid;
    }
}

package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BidsUpdateStatusDetailsResponseDto {

    private BidUpdateDto bid;

    @JsonCreator
    public BidsUpdateStatusDetailsResponseDto(@JsonProperty("bid") final BidUpdateDto bid) {
        this.bid = bid;
    }
}

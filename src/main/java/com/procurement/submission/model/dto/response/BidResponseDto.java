package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.submission.model.ocds.Bid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BidResponseDto {

    private String token;

    private String bidId;

    private Bid bid;

    @JsonCreator
    public BidResponseDto(@JsonProperty("token") final String token,
                          @JsonProperty("bidId") final String bidId,
                          @JsonProperty("bid") final Bid bid) {
        this.token = token;
        this.bidId = bidId;
        this.bid = bid;
    }
}

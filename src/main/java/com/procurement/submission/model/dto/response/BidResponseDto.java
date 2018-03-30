package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.submission.model.ocds.Bid;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
        "token",
        "bidId",
        "bid"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BidResponseDto {

    @JsonProperty("token")
    private String token;

    @JsonProperty("bidId")
    private String bidId;

    @JsonProperty("bid")
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

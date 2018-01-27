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
        "bid"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BidResponseDto {
    @NotNull
    @JsonProperty("token")
    private String token;
    @Valid
    @NotNull
    @JsonProperty("bid")
    private Bid bid;

    @JsonCreator
    public BidResponseDto(@JsonProperty("token") final String token,
                          @JsonProperty("bid") final Bid bid) {
        this.token = token;
        this.bid = bid;
    }
}

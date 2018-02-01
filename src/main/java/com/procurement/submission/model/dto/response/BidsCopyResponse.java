package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.submission.model.ocds.Bid;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
    "bids"
})
public class BidsCopyResponse {
    @Valid
    @NotNull
    @JsonProperty("bids")
    private List<Bid> bids;

    @JsonCreator
    public BidsCopyResponse(@JsonProperty("bids") final List<Bid> bids) {
        this.bids = bids;
    }
}

package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class BidsUpdateFinalStatusResponse {

    @JsonProperty("bids")
    private List<BidUpdate> bids;

    @JsonCreator
    public BidsUpdateFinalStatusResponse(@JsonProperty("bids") @NotEmpty final List<BidUpdate> bids) {
        this.bids = bids;
    }
}

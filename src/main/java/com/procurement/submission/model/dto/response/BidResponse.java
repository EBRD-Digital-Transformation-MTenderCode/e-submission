package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    "ocid",
    "stage",
    "bidToken",
    "bid",
    "owner"
})
public class BidResponse {
    @NotNull
    @JsonProperty("ocid")
    private String ocid;

    @NotNull
    @JsonProperty("stage")
    private String stage;

    @NotNull
    @JsonProperty("bidToken")
    private String bidToken;

    @Valid
    @NotNull
    @JsonProperty("bid")
    private Bid bid;

    @NotNull
    @JsonProperty("owner")
    private String owner;

    @JsonCreator
    public BidResponse(@JsonProperty("ocid") final String ocid,
                       @JsonProperty("stage") final String stage,
                       @JsonProperty("bidToken") final String bidToken,
                       @JsonProperty("bid") final Bid bid,
                       @JsonProperty("owner") final String owner) {
        this.ocid = ocid;
        this.stage = stage;
        this.bidToken = bidToken;
        this.bid = bid;
        this.owner = owner;
    }
}

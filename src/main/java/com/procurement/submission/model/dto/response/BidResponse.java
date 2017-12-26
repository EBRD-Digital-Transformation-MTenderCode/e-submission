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
    "bid"
})
public class BidResponse {
    @NotNull
    @JsonProperty("ocid")
    private String ocid;

    @NotNull
    @JsonProperty("stage")
    private String stage;

    @Valid
    @NotNull
    @JsonProperty("bid")
    private Bid bid;

    @JsonCreator
    public BidResponse(@JsonProperty("ocid") final String ocid,
                       @JsonProperty("stage") final String stage,
                       @JsonProperty("bid") final Bid bid) {
        this.ocid = ocid;
        this.stage = stage;
        this.bid = bid;
    }
}

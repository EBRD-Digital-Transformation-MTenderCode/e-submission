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
    "ocid",
    "bid"
})
public class BidsCopyResponse {
    @NotNull
    @JsonProperty("ocid")
    private String ocid;

    @Valid
    @NotNull
    @JsonProperty("bids")
    private List<Bid> bids;

    @JsonCreator
    public BidsCopyResponse(@JsonProperty("ocid") final String ocid,
                            @JsonProperty("bids") final List<Bid> bids) {
        this.ocid = ocid;
        this.bids = bids;
    }
}

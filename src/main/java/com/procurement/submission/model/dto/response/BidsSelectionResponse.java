package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.submission.model.ocds.Bid;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BidsSelectionResponse {

    @Valid
    @NotEmpty
    @JsonProperty("bids")
    private List<Bid> bids;

    @JsonCreator
    public BidsSelectionResponse(@JsonProperty("bids") final List<Bid> bids) {
        this.bids = bids;
    }
}

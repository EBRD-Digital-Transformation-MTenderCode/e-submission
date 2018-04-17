package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.submission.model.ocds.Bid;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BidsSelectionResponseDto {

    private List<Bid> bids;

    @JsonCreator
    public BidsSelectionResponseDto(@JsonProperty("bids") final List<Bid> bids) {
        this.bids = bids;
    }
}

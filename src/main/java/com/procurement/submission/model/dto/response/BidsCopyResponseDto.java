package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.submission.model.ocds.Bids;
import com.procurement.submission.model.ocds.Period;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidsCopyResponseDto {

    @JsonProperty("bids")
    private Bids bids;

    @JsonProperty("tenderPeriod")
    private Period tenderPeriod;

    @JsonCreator
    public BidsCopyResponseDto(@JsonProperty("bids") final Bids bids,
                               @JsonProperty("tenderPeriod") final Period tenderPeriod) {
        this.bids = bids;
        this.tenderPeriod = tenderPeriod;
    }
}

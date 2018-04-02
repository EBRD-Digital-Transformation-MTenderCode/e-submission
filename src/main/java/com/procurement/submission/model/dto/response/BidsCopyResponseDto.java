package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.Bids;
import com.procurement.submission.model.ocds.Period;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

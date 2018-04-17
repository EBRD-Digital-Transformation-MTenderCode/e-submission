package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import com.procurement.submission.model.ocds.Period;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidsUpdateStatusResponseDto {

    private Period tenderPeriod;

    private Set<OrganizationReference> tenderers;

    private List<Bid> bids;

    @JsonCreator
    public BidsUpdateStatusResponseDto(@JsonProperty("tenderPeriod") final Period tenderPeriod,
                                       @JsonProperty("tenderers") final Set<OrganizationReference> tenderers,
                                       @JsonProperty("bids") final List<Bid> bids) {
        this.tenderPeriod = tenderPeriod;
        this.tenderers = tenderers;
        this.bids = bids;
    }
}

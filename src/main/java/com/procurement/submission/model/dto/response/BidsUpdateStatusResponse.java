package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "tenderPeriod",
        "tenderers",
        "bids"
})
public class BidsUpdateStatusResponse {

    @JsonProperty("tenderPeriod")
    private Period tenderPeriod;

    @JsonProperty("tenderers")
    private Set<OrganizationReference> tenderers;

    @JsonProperty("bids")
    private List<Bid> bids;

    @JsonCreator
    public BidsUpdateStatusResponse(@JsonProperty("tenderPeriod") @NotNull final Period tenderPeriod,
                                    @JsonProperty("tenderers") @NotEmpty final Set<OrganizationReference> tenderers,
                                    @JsonProperty("bids") @NotEmpty final List<Bid> bids) {
        this.tenderPeriod = tenderPeriod;
        this.tenderers = tenderers;
        this.bids = bids;
    }
}

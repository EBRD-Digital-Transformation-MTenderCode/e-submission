package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
public class BidsResponse {

    private List<BidResponse> bids;

    @JsonCreator
    public BidsResponse(@NotEmpty @JsonProperty("bids") final List<BidResponse> bids) {
        this.bids = bids;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(bids)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BidsResponse)) {
            return false;
        }
        final BidsResponse bidsObj = (BidsResponse) obj;
        return new EqualsBuilder().append(bids, bidsObj.getBids())
                                  .isEquals();
    }
}

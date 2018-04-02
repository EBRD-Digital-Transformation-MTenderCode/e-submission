package com.procurement.submission.model.ocds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@Setter
public class Bids {

    @JsonProperty("details")
    private List<Bid> details;


    @JsonCreator
    public Bids(@JsonProperty("details") final List<Bid> details) {
        this.details = details;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(details)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Bids)) {
            return false;
        }
        final Bids rhs = (Bids) other;
        return new EqualsBuilder()
                .append(details, rhs.details)
                .isEquals();
    }
}

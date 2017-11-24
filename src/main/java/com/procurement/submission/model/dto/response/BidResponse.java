package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
public class BidResponse {
    private String id;
    private List<Tenderer> tenderers;
    private List<RelatedLot> relatedLots;

    @JsonCreator
    public BidResponse(@JsonProperty("id") final String id,
                       @JsonProperty("tenderers") @NotEmpty final List<Tenderer> tenderers,
                       @JsonProperty("relatedLots") @NotEmpty final List<RelatedLot> relatedLots) {
        this.id = id;
        this.tenderers = tenderers;
        this.relatedLots = relatedLots;
    }

    @Getter
    public static class Tenderer {
        private String id;
        private String scheme;

        @JsonCreator
        public Tenderer(@NotNull @JsonProperty("id") final String id,
                        @NotNull @JsonProperty("scheme") final String scheme) {
            this.id = id;
            this.scheme = scheme;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id)
                                        .append(scheme)
                                        .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Tenderer)) {
                return false;
            }
            Tenderer tenderer = (Tenderer) obj;
            return new EqualsBuilder().append(id, tenderer.getId())
                                      .append(scheme, tenderer.getScheme())
                                      .isEquals();
        }
    }

    @Getter
    public static class RelatedLot {
        private String id;

        @JsonCreator
        public RelatedLot(@NotNull @JsonProperty("id") final String id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id)
                                        .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof RelatedLot)) {
                return false;
            }
            RelatedLot relatedLot = (RelatedLot) obj;
            return new EqualsBuilder().append(id, relatedLot.getId())
                                      .isEquals();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(tenderers)
                                    .append(relatedLots)
                                    .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BidResponse)) {
            return false;
        }
        final BidResponse bidResponse = (BidResponse) obj;
        return new EqualsBuilder().append(id, bidResponse.id)
                                  .append(tenderers, bidResponse.tenderers)
                                  .append(relatedLots, bidResponse.relatedLots)
                                  .isEquals();
    }
}

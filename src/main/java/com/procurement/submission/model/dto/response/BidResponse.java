package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import com.procurement.submission.model.dto.request.BidStatus;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
public class BidResponse {
    private String bidId;
    private LocalDateTime date;
    private BidStatus status;
    private List<Tenderer> tenderers;
    private List<RelatedLot> relatedLots;

    @JsonCreator
    public BidResponse(@JsonProperty("bidId") @NotNull final String id,
                       @JsonSerialize(using = LocalDateTimeSerializer.class)
                       @JsonProperty("date") @NotNull final LocalDateTime date,
                       @JsonProperty("status") @NotNull final BidStatus status,
                       @JsonProperty("tenderers") @NotEmpty final List<Tenderer> tenderers,
                       @JsonProperty("relatedLots") @NotEmpty final List<RelatedLot> relatedLots) {
        this.bidId = id;
        this.date = date;
        this.status = status;
        this.tenderers = tenderers;
        this.relatedLots = relatedLots;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(bidId)
                                    .append(date)
                                    .append(status)
                                    .append(tenderers)
                                    .append(relatedLots)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BidResponse)) {
            return false;
        }
        final BidResponse bidResponse = (BidResponse) obj;
        return new EqualsBuilder().append(bidId, bidResponse.bidId)
                                  .append(date, bidResponse.date)
                                  .append(status, bidResponse.status)
                                  .append(tenderers, bidResponse.tenderers)
                                  .append(relatedLots, bidResponse.relatedLots)
                                  .isEquals();
    }

    @Getter
    public static class Tenderer {
        private String id;
        private String name;
        private String scheme;

        @JsonCreator
        public Tenderer(@NotNull @JsonProperty("id") final String id,
                        @NotNull @JsonProperty("name") final String name,
                        @NotNull @JsonProperty("scheme") final String scheme) {
            this.id = id;
            this.name = name;
            this.scheme = scheme;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id)
                                        .append(name)
                                        .append(scheme)
                                        .toHashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Tenderer)) {
                return false;
            }
            final Tenderer tenderer = (Tenderer) obj;
            return new EqualsBuilder().append(id, tenderer.getId())
                                      .append(name, tenderer.getName())
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
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof RelatedLot)) {
                return false;
            }
            final RelatedLot relatedLot = (RelatedLot) obj;
            return new EqualsBuilder().append(id, relatedLot.getId())
                                      .isEquals();
        }
    }
}

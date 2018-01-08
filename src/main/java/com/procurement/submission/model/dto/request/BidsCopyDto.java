package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "ocid",
    "stage",
    "previousStage",
    "lots"
})
public class BidsCopyDto {
    private String ocId;
    private String stage;
    private String previousStage;
    private List<Lot> lots;

    @JsonCreator
    public BidsCopyDto(@JsonProperty("ocid") final String ocId,
                       @JsonProperty("stage") final String stage,
                       @JsonProperty("previousStage") final String previousStage,
                       @JsonProperty("lots") final List<Lot> lots) {
        this.ocId = ocId;
        this.stage = stage;
        this.previousStage = previousStage;
        this.lots = lots;
    }

    @Getter
    @JsonPropertyOrder({
        "id"
    })
    public static class Lot {
        private String id;

        @JsonCreator
        public Lot(@JsonProperty("id") final String id) {
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
            if (!(obj instanceof Lot)) {
                return false;
            }
            final Lot lot = (Lot) obj;
            return new EqualsBuilder().append(id, lot.id)
                                      .isEquals();
        }
    }
}

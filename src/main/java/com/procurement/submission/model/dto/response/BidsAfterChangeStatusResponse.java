package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import com.procurement.submission.model.dto.request.BidStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class BidsAfterChangeStatusResponse {

    @JsonProperty("bids") List<Bid> bids;

    @JsonCreator
    public BidsAfterChangeStatusResponse(@JsonProperty("bids") final List<Bid> bids) {
        this.bids = bids;
    }

    @Getter
    public static class Bid{
        @JsonProperty("id")
        private String id;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonProperty("date")
        private LocalDateTime date;
        @JsonProperty("status")
        private BidStatus status;
        @JsonProperty("tenderers")
        private List<Tenderer> tenderers;
        @JsonProperty("relatedLots")
        private List<String> relatedLots;

        @JsonCreator
        public Bid(@JsonProperty("id") final String id,
                   @JsonProperty("date") final LocalDateTime date,
                   @JsonProperty("status") final BidStatus status,
                   @JsonProperty("tenderers") final List<Tenderer> tenderers,
                   @JsonProperty("relatedLots") final List<String> relatedLots) {
            this.id = id;
            this.date = date;
            this.status = status;
            this.tenderers = tenderers;
            this.relatedLots = relatedLots;
        }

        @Getter
        public static class Tenderer{
            @JsonProperty("id")
            private String id;
            @JsonProperty("name")
            private String name;

            @JsonCreator
            public Tenderer(@JsonProperty("id") final String id,
                            @JsonProperty("name") final String name) {
                this.id = id;
                this.name = name;
            }
        }
    }
}

package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import com.procurement.submission.model.ocds.Value;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BidsSelectionResponse {

    @Valid
    @NotEmpty
    @JsonProperty("bids")
    private List<Bid> bids;

    @JsonCreator
    public BidsSelectionResponse(@JsonProperty("bids") final List<Bid> bids) {
        this.bids = bids;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonPropertyOrder({
            "id",
            "relatedLots",
            "date",
            "createdDate",
            "pendingDate",
            "value",
            "tenderers"
    })
    public static class Bid {
        private String id;
        private List<String> relatedLots;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime date;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime createdDate;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime pendingDate;
        private Value value;
        private List<OrganizationReferenceRs> tenderers;
    }
}

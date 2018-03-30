package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.Document;
import com.procurement.submission.model.ocds.Value;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "id",
    "date",
    "status",
    "statusDetails",
    "tenderers",
    "value",
    "documents",
    "relatedLots"
})
public class BidUpdate {
    private String id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    private Bid.Status status;
    private Bid.StatusDetails statusDetails;
    private List<OrganizationReferenceRs> tenderers;
    private Value value;
    private List<Document> documents;
    private List<String> relatedLots;

    @JsonCreator
    public BidUpdate(@JsonProperty("id") final String id,
                     @JsonProperty("date") final LocalDateTime date,
                     @JsonProperty("status") final Bid.Status status,
                     @JsonProperty("statusDetails") final Bid.StatusDetails statusDetails,
                     @JsonProperty("tenderers") final List<OrganizationReferenceRs> tenderers,
                     @JsonProperty("value") final Value value,
                     @JsonProperty("documents") final List<Document> documents,
                     @JsonProperty("relatedLots") final List<String> relatedLots) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.statusDetails = statusDetails;
        this.relatedLots = relatedLots;
        this.value = value;
        this.documents = documents;
        this.tenderers = tenderers;
    }
}

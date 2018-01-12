package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({
    "id",
    "date",
    "status",
    "tenderers",
    "value",
    "documents",
    "relatedLots"
})
public class BidWithdrawnRs {
    private String id;
    private LocalDateTime date;
    private Bid.Status status;
    private List<OrganizationReferenceRs> tenderers;
    private Value value;
    private List<Document> documents;
    private List<String> relatedLots;

    @JsonCreator
    public BidWithdrawnRs(@JsonProperty("id") @NotNull final String id,
                          @JsonProperty("date") @NotNull final LocalDateTime date,
                          @JsonProperty("status") @NotNull final Bid.Status status,
                          @JsonProperty("tenderers") @NotEmpty @Valid final List<OrganizationReferenceRs> tenderers,
                          @JsonProperty("value") @Valid final Value value,
                          @JsonProperty("documents") @NotNull @Valid final List<Document> documents,
                          @JsonProperty("relatedLots") @NotNull final List<String> relatedLots) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.relatedLots = relatedLots;
        this.value = value;
        this.documents = documents;
        this.tenderers = tenderers;
    }
}

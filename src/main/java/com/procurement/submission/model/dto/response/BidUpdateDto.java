package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import com.procurement.submission.model.ocds.Document;
import com.procurement.submission.model.ocds.Status;
import com.procurement.submission.model.ocds.StatusDetails;
import com.procurement.submission.model.ocds.Value;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BidUpdateDto {

    private String id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    private Status status;
    private StatusDetails statusDetails;
    private List<OrganizationReferenceDto> tenderers;
    private Value value;
    private List<Document> documents;
    private List<String> relatedLots;

    @JsonCreator
    public BidUpdateDto(@JsonProperty("id") final String id,
                        @JsonProperty("date") final LocalDateTime date,
                        @JsonProperty("status") final Status status,
                        @JsonProperty("statusDetails") final StatusDetails statusDetails,
                        @JsonProperty("tenderers") final List<OrganizationReferenceDto> tenderers,
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

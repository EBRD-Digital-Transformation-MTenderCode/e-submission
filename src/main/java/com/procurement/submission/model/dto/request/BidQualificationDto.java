package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.submission.databinding.LocalDateTimeDeserializer;
import com.procurement.submission.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
    "id",
    "date",
    "status",
    "tenderers",
    "documents",
    "relatedLots"
})
public class BidQualificationDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime date;

    @NotNull
    @JsonProperty("status")
    private String status;

    @NotEmpty
    @JsonProperty("tenderers")
    private List<OrganizationReferenceDto> tenderers;

    @NotEmpty
    @JsonProperty("documents")
    private List<DocumentDto> documents;

    @NotEmpty
    @JsonProperty("relatedLots")
    private List<String> relatedLots;

    @JsonCreator
    public BidQualificationDto(
        @JsonProperty("id") final String id,
        @JsonProperty("date") final LocalDateTime date,
        @JsonProperty("status") final String status,
        @JsonProperty("tenderers") final List<OrganizationReferenceDto> tenderers,
        @JsonProperty("documents") final List<DocumentDto> documents,
        @JsonProperty("relatedLots") final List<String> relatedLots) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.tenderers = tenderers;
        this.documents = documents;
        this.relatedLots = relatedLots;
    }
}

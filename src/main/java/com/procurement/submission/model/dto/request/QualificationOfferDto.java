package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "status",
    "tenderers",
    "documents",
    "relatedLots"
})
public class QualificationOfferDto {

    @NotNull
    @JsonProperty("tenderId")
    private String tenderId;

    @NotNull
    @JsonProperty("ocid")
    private String ocid;

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
    public QualificationOfferDto(@JsonProperty("status") final String status,
                                 @JsonProperty("tenderers") final List<OrganizationReferenceDto> tenderers,
                                 @JsonProperty("documents") final List<DocumentDto> documents,
                                 @JsonProperty("relatedLots") final List<String> relatedLots) {
        this.status = status;
        this.tenderers = tenderers;
        this.documents = documents;
        this.relatedLots = relatedLots;
    }
}

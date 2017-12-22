package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.submission.model.ocds.BidStatus;
import com.procurement.submission.model.ocds.Document;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "id",
    "status",
    "documents"
})
public class BidAqpDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private BidStatus status;

    @Valid
    @JsonProperty("documents")
    private List<Document> documents;

    @JsonCreator
    public BidAqpDto(@JsonProperty("id") final String id,
                     @JsonProperty("status") final BidStatus status,
                     @JsonProperty("documents") final List<Document> documents) {
        this.id = id;
        this.status = status;
        this.documents = documents;
    }
}


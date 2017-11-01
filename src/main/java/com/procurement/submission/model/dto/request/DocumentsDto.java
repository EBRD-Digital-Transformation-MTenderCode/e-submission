package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class DocumentsDto {

    @JsonProperty("documents")
    private List<DocumentDto> documents;

    @JsonCreator
    public DocumentsDto(@JsonProperty("documents") final List<DocumentDto> documents) {
        this.documents = documents;
    }

    // TODO: 01.11.17 equals and hash code
}

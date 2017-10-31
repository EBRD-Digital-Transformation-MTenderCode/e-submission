package com.procurement.submission.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import lombok.Getter;

@Getter
public class DocumentBidSubmissionDto {

    private String id;
    private String documentType;
    private String title;
    private String description;
    private String url;
    private String datePublished;
    private String dateModified;
    private String format;
    private String language;
    private List<String> relatedLots;

    @JsonCreator
    public DocumentBidSubmissionDto(String id,
                                    String documentType,
                                    String title,
                                    String description,
                                    String url,
                                    String datePublished,
                                    String dateModified,
                                    String format,
                                    String language,
                                    List<String> relatedLots) {
        this.id = id;
        this.documentType = documentType;
        this.title = title;
        this.description = description;
        this.url = url;
        this.datePublished = datePublished;
        this.dateModified = dateModified;
        this.format = format;
        this.language = language;
        this.relatedLots = relatedLots;
    }
}

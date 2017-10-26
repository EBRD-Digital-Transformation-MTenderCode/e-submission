package com.procurement.submission.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class DocumentDto {
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
}
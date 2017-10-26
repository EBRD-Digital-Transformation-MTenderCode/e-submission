package com.procurement.submission.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class Document {
    public String id;
    public String documentType;
    public String title;
    public String description;
    public String url;
    public String datePublished;
    public String dateModified;
    public String format;
    public String language;
    public List<String> relatedLots;
}
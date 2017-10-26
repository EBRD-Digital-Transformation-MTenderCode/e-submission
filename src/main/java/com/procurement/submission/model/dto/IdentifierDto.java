package com.procurement.submission.model.dto;

import lombok.Data;

@Data
public class IdentifierDto {
    private String scheme;
    private String id;
    private String legalName;
    private String uri;
}
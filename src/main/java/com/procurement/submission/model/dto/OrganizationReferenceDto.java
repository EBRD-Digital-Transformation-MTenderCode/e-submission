package com.procurement.submission.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrganizationReferenceDto {
    private String name;
    private Integer id;
    private IdentifierDto identifier;
    private AddressDto address;
    private List<IdentifierDto> additionalIdentifiers;
    private ContactPointDto contactPoint;
}
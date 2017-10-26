package com.procurement.submission.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class BidDto {
    private String id;
    private String date;
    private String status;
    private List<OrganizationReferenceDto> tenderers;
    private ValueDto value;
    private List<DocumentDto> documents;
    private List<String> relatedLots;
    private List<RequirementResponseDto> requirementResponses;
}

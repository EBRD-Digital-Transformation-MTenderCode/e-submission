package com.procurement.submission.model.dto;

import lombok.Data;

@Data
public class RequirementResponseDto {
    private String id;
    private String title;
    private String description;
    private Object value;
    private PeriodDto period;
    private RequirementReferenceDto requirement;
    private OrganizationReferenceDto relatedTenderer;
}

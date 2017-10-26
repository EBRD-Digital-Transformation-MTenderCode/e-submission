package com.procurement.submission.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class Bid {
    public String id;
    public String date;
    public String status;
    public List<OrganizationReference> tenderers;
    public Value value;
    public List<Document> documents;
    public List<String> relatedLots;
    public List<RequirementResponse> requirementResponses;
}

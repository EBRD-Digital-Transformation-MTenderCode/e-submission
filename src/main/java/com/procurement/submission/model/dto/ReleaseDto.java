package com.procurement.submission.model.dto;

import lombok.Data;

@Data
public class ReleaseDto {
    private String ocid;
    private BidsDto bids;
}

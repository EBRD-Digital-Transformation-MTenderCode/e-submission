package com.procurement.submission.model.dto.request;

import lombok.Getter;

@Getter
public class BidsGetDto {

    private String ocid;
    private String procurementMethodDetail;
    private String stage;
    private String country;

    public BidsGetDto(final String ocid,
                      final String procurementMethodDetail,
                      final String stage,
                      final String country) {
        this.ocid = ocid;
        this.procurementMethodDetail = procurementMethodDetail;
        this.stage = stage;
        this.country = country;
    }
}

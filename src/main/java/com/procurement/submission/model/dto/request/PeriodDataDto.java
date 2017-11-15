package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "ocId",
    "country",
    "procurementMethodDetails",
    "tenderPeriod"
})
public class PeriodDataDto {

    @JsonProperty("ocid")
    private String ocId;

    @NotNull
    @JsonProperty("country")
    private String country;

    @NotNull
    @JsonProperty("procurementMethodDetails")
    private String procurementMethodDetails;

    @JsonProperty("tenderPeriod")
    private TenderPeriodDto tenderPeriod;

    @JsonCreator
    public PeriodDataDto(@JsonProperty("ocid") final String ocId,
                         @JsonProperty("country") final String country,
                         @JsonProperty("procurementMethodDetails") final String procurementMethodDetails,
                         @JsonProperty("tenderPeriod") final TenderPeriodDto tenderPeriod) {
        this.ocId = ocId;
        this.country = country;
        this.procurementMethodDetails = procurementMethodDetails;
        this.tenderPeriod = tenderPeriod;
    }
}

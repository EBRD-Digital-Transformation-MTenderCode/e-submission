package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "ocid",
    "stage",
    "country",
    "procurementMethodDetails"
})
public class BidsSelectionDto {
    private String ocId;
    private String stage;
    private String country;
    private String method;


    @JsonCreator
    public BidsSelectionDto(@JsonProperty("ocid") final String ocId,
                            @JsonProperty("stage") final String stage,
                            @JsonProperty("country") final String country,
                            @JsonProperty("procurementMethodDetails") final String method) {
        this.ocId = ocId;
        this.stage = stage;
        this.country = country;
        this.method = method;
    }
}

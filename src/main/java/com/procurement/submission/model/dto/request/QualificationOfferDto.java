package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "ocid",
    "bid"
})
public class QualificationOfferDto {

    @NotNull
    @JsonProperty("ocid")
    private String ocid;

    @NotNull
    @JsonProperty("bid")
    private BidQualificationDto bid;

    @JsonCreator
    public QualificationOfferDto(@JsonProperty("ocid") final String ocid,
                                 @JsonProperty("bid") final BidQualificationDto bid) {
        this.ocid = ocid;
        this.bid = bid;
    }
}

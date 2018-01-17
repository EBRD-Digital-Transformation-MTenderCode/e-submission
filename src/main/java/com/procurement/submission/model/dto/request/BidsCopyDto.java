package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "ocid",
    "stage",
    "previousStage",
    "lots"
})
public class BidsCopyDto {
    private String ocId;
    private String stage;
    private String previousStage;
    private List<LotDto> lots;

    @JsonCreator
    public BidsCopyDto(@JsonProperty("ocid") final String ocId,
                       @JsonProperty("stage") final String stage,
                       @JsonProperty("previousStage") final String previousStage,
                       @JsonProperty("lots") final List<LotDto> lots) {
        this.ocId = ocId;
        this.stage = stage;
        this.previousStage = previousStage;
        this.lots = lots;
    }
}

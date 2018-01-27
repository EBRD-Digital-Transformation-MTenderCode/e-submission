package com.procurement.submission.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "lots"
})
public class BidsCopyDto {

    private List<LotDto> lots;

    @JsonCreator
    public BidsCopyDto(@JsonProperty("lots") final List<LotDto> lots) {
        this.lots = lots;
    }
}

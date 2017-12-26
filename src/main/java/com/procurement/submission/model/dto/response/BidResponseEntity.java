package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "success",
    "responseDetails",
    "data"
})
public class BidResponseEntity {
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("responseDetails")
    private List<Detail> responseDetails;
    @JsonProperty("data")
    private BidResponse data;

    @JsonCreator
    public BidResponseEntity(@JsonProperty("success") boolean success,
                             @JsonProperty("responseDetails") List<Detail> responseDetails,
                             @JsonProperty("data") BidResponse data) {
        this.success = success;
        this.responseDetails = responseDetails;
        this.data = data;
    }

    @Getter
    @JsonPropertyOrder({
        "code",
        "message"
    })
    public static class Detail {
        @JsonProperty("code")
        private int code;
        @JsonProperty("message")
        private String message;

        public Detail(@JsonProperty("code") final int code,
                      @JsonProperty("message") final String message) {
            this.code = code;
            this.message = message;
        }
    }
}

package com.procurement.submission.model.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "success",
    "responseDetails",
    "data"
})
public class BidsCopyResponseEntity {
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("responseDetails")
    private List<Detail> responseDetails;
    @JsonProperty("data")
    private BidsCopyResponse data;

    @JsonCreator
    public BidsCopyResponseEntity(@JsonProperty("success") final boolean success,
                                  @JsonProperty("responseDetails") final List<Detail> responseDetails,
                                  @JsonProperty("data") final BidsCopyResponse data) {
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

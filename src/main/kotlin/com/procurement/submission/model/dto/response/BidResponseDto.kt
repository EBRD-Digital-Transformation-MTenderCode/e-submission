package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Bid

data class BidResponseDto(

        @JsonProperty("token")
        val token: String,

        @JsonProperty("bidId")
        val bidId: String,

        @JsonProperty("bid")
        val bid: Bid
)

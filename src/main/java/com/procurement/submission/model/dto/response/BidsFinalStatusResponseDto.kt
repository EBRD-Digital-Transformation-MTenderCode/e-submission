package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Bid

data class BidsFinalStatusResponseDto(

        @JsonProperty("bids")
        val bids: List<Bid>
)

package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.ocds.Bid
import com.procurement.submission.model.ocds.Period

data class BidsUpdateStatusResponseDto(

        @JsonProperty("tenderPeriod")
        val tenderPeriod: Period,

        @JsonProperty("bids")
        val bids: List<Bid>
)

package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Bids
import com.procurement.submission.model.dto.ocds.Period

data class BidsCopyResponseDto(

        @JsonProperty("bids")
        val bids: Bids,

        @JsonProperty("tenderPeriod")
        val tenderPeriod: Period
)

package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty

data class Bids(
        @JsonProperty("details")
        val details: List<Bid>?
)
package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.ocds.Bid

data class BidsSelectionResponseDto(

        @JsonProperty("bids")
        val bids: List<Bid>
)

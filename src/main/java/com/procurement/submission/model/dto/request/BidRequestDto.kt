package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.ocds.Bid
import javax.validation.constraints.NotNull

data class BidRequestDto(

        @JsonProperty("bid") @NotNull
        val bid: Bid
)

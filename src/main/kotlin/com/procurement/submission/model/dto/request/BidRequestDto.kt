package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Bid
import javax.validation.constraints.NotNull

data class BidRequestDto @JsonCreator constructor(

        @field:NotNull
        val bid: Bid
)

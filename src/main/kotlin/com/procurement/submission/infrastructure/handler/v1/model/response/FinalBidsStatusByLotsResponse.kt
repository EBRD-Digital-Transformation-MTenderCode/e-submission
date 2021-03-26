package com.procurement.submission.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.Status
import java.util.*

data class FinalBidsStatusByLotsResponse(
    @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: List<Bid>
) {

    data class Bid(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: Status
    )
}
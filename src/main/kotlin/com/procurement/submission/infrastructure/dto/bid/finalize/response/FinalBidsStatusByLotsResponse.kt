package com.procurement.submission.infrastructure.dto.bid.finalize.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails
import java.util.*

data class FinalBidsStatusByLotsResponse(
    @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: List<Bid>
) {

    data class Bid(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: Status,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails
    )
}
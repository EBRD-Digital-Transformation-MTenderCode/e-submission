package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.infrastructure.handler.v2.model.response.FinalizeBidsByAwardsResult.Bids
import com.procurement.submission.model.dto.ocds.Bid

data class FinalizeBidsByAwardsResult(
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
) {
    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: String
        ) { companion object {} }
    }
}

fun Bids.Detail.Companion.fromDomain(bid: Bid): Bids.Detail =
    Bids.Detail(id = bid.id, status = bid.status.key)

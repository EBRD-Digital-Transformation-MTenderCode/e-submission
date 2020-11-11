package com.procurement.submission.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.lot.LotId

data class GetBidsAuctionRequest(
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {
    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
    )
}
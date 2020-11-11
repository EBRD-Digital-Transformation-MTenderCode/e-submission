package com.procurement.submission.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.lot.LotId

data class GetBidsByLotsRequest(
    @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
) {
    data class Lot(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: LotId
    )
}
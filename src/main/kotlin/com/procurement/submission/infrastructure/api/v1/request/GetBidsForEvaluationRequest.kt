package com.procurement.submission.infrastructure.api.v1.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class GetBidsForEvaluationRequest(
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {
    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID
    )
}
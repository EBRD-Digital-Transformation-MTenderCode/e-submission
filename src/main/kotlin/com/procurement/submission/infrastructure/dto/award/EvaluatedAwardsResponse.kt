package com.procurement.submission.infrastructure.dto.award

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.StatusDetails
import java.util.*

data class EvaluatedAwardsResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: List<Bid>
) {
    data class Bid(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails
    )
}

package com.procurement.submission.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.AwardStatusDetails
import java.util.*

data class ApplyEvaluatedAwardsRequest(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: UUID
    )
}

package com.procurement.submission.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails

data class SetStateForBidsResult(
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
) {
    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: Status,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: StatusDetails
        )
    }
}
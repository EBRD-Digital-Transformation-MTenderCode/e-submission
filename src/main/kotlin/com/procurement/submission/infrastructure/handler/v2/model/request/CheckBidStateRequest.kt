package com.procurement.submission.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckBidStateRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids,
    @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: String,
    @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
    @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: String
) {
    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}
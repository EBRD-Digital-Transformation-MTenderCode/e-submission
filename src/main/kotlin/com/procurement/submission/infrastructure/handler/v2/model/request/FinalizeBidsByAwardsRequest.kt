package com.procurement.submission.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class FinalizeBidsByAwardsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String,
        @param:JsonProperty("relatedBid") @field:JsonProperty("relatedBid") val relatedBid: String
    )
}
package com.procurement.submission.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAccessToBidRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids,
    @param:JsonProperty("token") @field:JsonProperty("token") val token: String,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String
) {
    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}
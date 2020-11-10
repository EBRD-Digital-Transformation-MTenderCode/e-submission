package com.procurement.submission.infrastructure.api.v1.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class BidCreateResponse(
    @field:JsonProperty("bid") @param:JsonProperty("bid") val bid: Bid
) {
    data class Bid(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("token") @param:JsonProperty("token") val token: UUID
    )
}
package com.procurement.submission.infrastructure.dto.bid.opendoc.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenBidDocsRequest(
    @field:JsonProperty("nextAwardForUpdate") @param:JsonProperty("nextAwardForUpdate") val nextAwardForUpdate: NextAwardForUpdate
) {
    data class NextAwardForUpdate(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: String
    )
}
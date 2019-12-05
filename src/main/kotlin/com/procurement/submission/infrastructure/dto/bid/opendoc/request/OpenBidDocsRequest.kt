package com.procurement.submission.infrastructure.dto.bid.opendoc.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.award.AwardId
import com.procurement.submission.domain.model.bid.BidId

data class OpenBidDocsRequest(
    @field:JsonProperty("nextAwardForUpdate") @param:JsonProperty("nextAwardForUpdate") val nextAwardForUpdate: NextAwardForUpdate
) {
    data class NextAwardForUpdate(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId
    )
}

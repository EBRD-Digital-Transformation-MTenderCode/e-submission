package com.procurement.submission.infrastructure.api.v1.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.AwardCriteriaDetails
import com.procurement.submission.domain.model.enums.AwardStatusDetails

data class OpenBidsForPublishingRequest(
    @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails,
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId?
    )
}

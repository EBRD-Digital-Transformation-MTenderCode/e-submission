package com.procurement.submission.application.model.data.bid.open

import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.AwardCriteriaDetails
import com.procurement.submission.domain.model.enums.AwardStatusDetails

data class OpenBidsForPublishingData(
    val awardCriteriaDetails: AwardCriteriaDetails,
    val awards: List<Award>
) {
    data class Award(
        val statusDetails: AwardStatusDetails,
        val relatedBid: BidId?
    )
}

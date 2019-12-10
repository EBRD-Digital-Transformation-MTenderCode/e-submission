package com.procurement.submission.application.service.bid.opendoc

import com.procurement.submission.domain.model.award.AwardId
import com.procurement.submission.domain.model.bid.BidId

data class OpenBidDocsData(
    val nextAwardForUpdate: NextAwardForUpdate
) {
    data class NextAwardForUpdate(
        val id: AwardId,
        val relatedBid: BidId
    )
}

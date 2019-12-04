package com.procurement.submission.application.service.bid.opendoc

data class OpenBidDocsData(
    val nextAwardForUpdate: NextAwardForUpdate
) {
    data class NextAwardForUpdate(
        val id: String,
        val relatedBid: String
    )
}

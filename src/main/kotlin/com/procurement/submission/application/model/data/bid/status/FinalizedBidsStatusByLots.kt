package com.procurement.submission.application.model.data.bid.status

import com.procurement.submission.domain.model.enums.BidStatus
import java.util.*

data class FinalizedBidsStatusByLots(
    val bids: List<Bid>
) {

    data class Bid(
        val id: UUID,
        val status: BidStatus
    )
}

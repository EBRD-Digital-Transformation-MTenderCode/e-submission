package com.procurement.submission.application.model.data.bid.status

import com.procurement.submission.domain.model.enums.Status
import java.util.*

data class FinalizedBidsStatusByLots(
    val bids: List<Bid>
) {

    data class Bid(
        val id: UUID,
        val status: Status
    )
}

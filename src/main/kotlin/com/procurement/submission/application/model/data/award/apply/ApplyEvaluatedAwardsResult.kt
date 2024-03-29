package com.procurement.submission.application.model.data.award.apply

import com.procurement.submission.domain.model.enums.BidStatusDetails
import java.util.*

data class ApplyEvaluatedAwardsResult(val bids: List<Bid>) {
    data class Bid(
        val id: UUID,
        val statusDetails: BidStatusDetails
    )
}

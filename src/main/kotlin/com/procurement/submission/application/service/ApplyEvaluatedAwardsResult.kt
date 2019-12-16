package com.procurement.submission.application.service

import com.procurement.submission.domain.model.enums.StatusDetails
import java.util.*

data class ApplyEvaluatedAwardsResult(val bids: List<Bid>) {
    data class Bid(
        val id: UUID,
        val statusDetails: StatusDetails
    )
}

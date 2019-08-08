package com.procurement.submission.application.service

import com.procurement.submission.model.dto.ocds.StatusDetails
import java.util.*

data class AppliedEvaluatedAwardsData(val bids: List<Bid>) {
    data class Bid(
        val id: UUID,
        val statusDetails: StatusDetails
    )
}

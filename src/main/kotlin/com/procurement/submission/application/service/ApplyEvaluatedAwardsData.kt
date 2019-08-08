package com.procurement.submission.application.service

import com.procurement.submission.model.dto.ocds.AwardStatusDetails
import java.util.*

data class ApplyEvaluatedAwardsData(val awards: List<Award>) {
    data class Award(
        val statusDetails: AwardStatusDetails,
        val relatedBid: UUID
    )
}

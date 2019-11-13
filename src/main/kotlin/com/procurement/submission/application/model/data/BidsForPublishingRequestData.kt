package com.procurement.submission.application.model.data

import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.enums.AwardCriteriaDetails
import com.procurement.submission.domain.model.enums.AwardStatusDetails
import java.time.LocalDateTime
import java.util.*

data class BidsForPublishingRequestData(
    val awardCriteriaDetails: AwardCriteriaDetails,
    val awards: List<Award>
) {
    data class Award(
        val id: String,
        val date: LocalDateTime,
        val status: String,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<UUID>,
        val relatedBid: UUID,
        val value: Money,
        val suppliers: List<Supplier>
    ) {
        data class Supplier(
            val id: String,
            val name: String
        )
    }
}
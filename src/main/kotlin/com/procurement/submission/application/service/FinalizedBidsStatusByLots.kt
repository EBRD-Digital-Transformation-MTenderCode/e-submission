package com.procurement.submission.application.service

import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import java.util.*

data class FinalizedBidsStatusByLots(
    val bids: List<Bid>
) {

    data class Bid(
        val id: UUID,
        val status: Status,
        val statusDetails: StatusDetails
    )
}

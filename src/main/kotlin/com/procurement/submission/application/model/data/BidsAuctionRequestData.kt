package com.procurement.submission.application.model.data

import java.util.*

data class BidsAuctionRequestData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: UUID
    )
}
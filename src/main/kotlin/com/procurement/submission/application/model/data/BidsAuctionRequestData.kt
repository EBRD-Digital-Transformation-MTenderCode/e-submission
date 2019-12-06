package com.procurement.submission.application.model.data

import com.procurement.submission.domain.model.lot.LotId

data class BidsAuctionRequestData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: LotId
    )
}
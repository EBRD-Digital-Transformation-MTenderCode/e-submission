package com.procurement.submission.application.model.data.bid.get.bylots

import com.procurement.submission.domain.model.lot.LotId

class GetBidsByLotsData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: LotId
    )
}
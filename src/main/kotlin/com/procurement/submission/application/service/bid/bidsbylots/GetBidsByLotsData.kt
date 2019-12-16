package com.procurement.submission.application.service.bid.bidsbylots

import com.procurement.submission.domain.model.lot.LotId

class GetBidsByLotsData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: LotId
    )
}
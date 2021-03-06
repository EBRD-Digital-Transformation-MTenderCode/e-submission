package com.procurement.submission.application.model.data.bid.get

import java.util.*

data class BidsForEvaluationRequestData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: UUID
    )
}
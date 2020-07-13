package com.procurement.submission.application.model.data.bid.status

import java.util.*

data class FinalBidsStatusByLotsData(val lots: List<Lot>) {

    data class Lot(val id: UUID)
}

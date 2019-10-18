package com.procurement.submission.application.service

import java.util.*

data class FinalBidsStatusByLotsData(val lots: List<Lot>) {

    data class Lot(val id: UUID)
}

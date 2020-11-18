package com.procurement.submission.application.model.data.tender.period

import java.time.LocalDateTime

data class ExtendTenderPeriodResult(
    val tenderPeriod: TenderPeriod
) {
    data class TenderPeriod(
        val startDate: LocalDateTime,
        val endDate: LocalDateTime
    )
}

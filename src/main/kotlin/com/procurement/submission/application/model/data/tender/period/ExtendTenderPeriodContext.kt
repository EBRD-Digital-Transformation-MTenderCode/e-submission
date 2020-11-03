package com.procurement.submission.application.model.data.tender.period

import com.procurement.submission.domain.model.enums.ProcurementMethod
import java.time.LocalDateTime

data class ExtendTenderPeriodContext(
    val cpid: String,
    val stage: String,
    val startDate: LocalDateTime,
    val country: String,
    val pmd: ProcurementMethod
)

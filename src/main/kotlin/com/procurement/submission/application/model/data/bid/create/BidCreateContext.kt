package com.procurement.submission.application.model.data.bid.create

import com.procurement.submission.domain.model.enums.ProcurementMethod
import java.time.LocalDateTime

data class BidCreateContext(
    val cpid: String,
    val owner: String,
    val stage: String,
    val startDate: LocalDateTime,
    val pmd: ProcurementMethod
)

package com.procurement.submission.application.model.data.tender.period

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.ProcurementMethod
import java.time.LocalDateTime

data class ExtendTenderPeriodContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val startDate: LocalDateTime,
    val country: String,
    val pmd: ProcurementMethod
)

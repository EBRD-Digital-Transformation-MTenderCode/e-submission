package com.procurement.submission.application.repository.period.model

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import java.time.LocalDateTime

data class PeriodEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)

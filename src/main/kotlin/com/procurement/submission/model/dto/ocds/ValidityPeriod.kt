package com.procurement.submission.model.dto.ocds

import java.time.LocalDateTime

data class ValidityPeriod(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?
)
package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime

data class ValidityPeriod @JsonCreator constructor(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?
)
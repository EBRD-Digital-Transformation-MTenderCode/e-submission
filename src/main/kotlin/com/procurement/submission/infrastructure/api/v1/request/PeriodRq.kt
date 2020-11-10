package com.procurement.submission.infrastructure.api.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Period

data class PeriodRq @JsonCreator constructor(

        val tenderPeriod: Period
)

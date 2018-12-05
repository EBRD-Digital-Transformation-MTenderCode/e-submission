package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Period

data class PeriodRq @JsonCreator constructor(

        val tenderPeriod: Period
)

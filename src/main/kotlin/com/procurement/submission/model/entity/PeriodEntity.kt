package com.procurement.submission.model.entity

import java.util.*

data class PeriodEntity(

        val cpId: String,

        val stage: String,

        val startDate: Date,

        val endDate: Date
)

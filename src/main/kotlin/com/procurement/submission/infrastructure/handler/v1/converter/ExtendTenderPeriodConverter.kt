package com.procurement.submission.infrastructure.handler.v1.converter

import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodResult
import com.procurement.submission.infrastructure.handler.v1.model.response.ExtendTenderPeriodResponse

fun ExtendTenderPeriodResult.convert() = ExtendTenderPeriodResponse(
    ExtendTenderPeriodResponse.TenderPeriod(
        startDate = tenderPeriod.startDate,
        endDate = tenderPeriod.endDate
    )
)

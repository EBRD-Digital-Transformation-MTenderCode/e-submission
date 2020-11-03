package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodResult
import com.procurement.submission.infrastructure.dto.tender.period.ExtendTenderPeriodResponse

fun ExtendTenderPeriodResult.convert() = ExtendTenderPeriodResponse(
    ExtendTenderPeriodResponse.TenderPeriod(
        startDate = tenderPeriod.startDate,
        endDate = tenderPeriod.endDate
    )
)
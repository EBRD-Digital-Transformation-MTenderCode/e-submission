package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.ValidateTenderPeriodParams
import com.procurement.submission.infrastructure.handler.v2.model.request.ValidateTenderPeriodRequest

fun ValidateTenderPeriodRequest.convert() = ValidateTenderPeriodParams.tryCreate(
    operationType = operationType,
    pmd = pmd,
    country = country,
    date = date,
    tender = ValidateTenderPeriodParams.Tender(
        tenderPeriod = tender.tenderPeriod.convert()
            .onFailure { return it }
    )
)

fun ValidateTenderPeriodRequest.Tender.TenderPeriod.convert() =
    ValidateTenderPeriodParams.Tender.TenderPeriod.tryCreate(endDate = endDate)


package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.params.SetTenderPeriodParams
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodRequest

fun SetTenderPeriodRequest.convert() = SetTenderPeriodParams.tryCreate(
    cpid = cpid,
    ocid = ocid,
    date = date,
    tender = SetTenderPeriodParams.Tender(
        tenderPeriod = tender.tenderPeriod
            .convert()
            .onFailure { return it }
    )
)

fun SetTenderPeriodRequest.Tender.TenderPeriod.convert() = SetTenderPeriodParams.Tender.TenderPeriod.tryCreate(endDate)
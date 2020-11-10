package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.params.CheckPeriodParams
import com.procurement.submission.infrastructure.api.v1.request.CheckPeriodRequest

fun CheckPeriodRequest.convert() = CheckPeriodParams.tryCreate(cpid = cpid, ocid = ocid, date = date)

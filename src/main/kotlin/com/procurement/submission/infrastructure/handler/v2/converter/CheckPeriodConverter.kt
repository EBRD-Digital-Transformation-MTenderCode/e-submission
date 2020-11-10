package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.CheckPeriodParams
import com.procurement.submission.infrastructure.handler.v1.model.request.CheckPeriodRequest

fun CheckPeriodRequest.convert() = CheckPeriodParams.tryCreate(cpid = cpid, ocid = ocid, date = date)

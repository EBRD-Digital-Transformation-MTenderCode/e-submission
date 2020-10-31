package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.params.CheckPeriodParams
import com.procurement.submission.model.dto.request.CheckPeriodRequest

fun CheckPeriodRequest.convert() = CheckPeriodParams.tryCreate(cpid = cpid, ocid = ocid, date = date)

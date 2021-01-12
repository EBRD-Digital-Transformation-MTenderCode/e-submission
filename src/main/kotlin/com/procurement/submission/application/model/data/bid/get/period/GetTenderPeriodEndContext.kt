package com.procurement.submission.application.model.data.bid.get.period

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

data class GetTenderPeriodEndContext(
    val cpid: Cpid,
    val ocid: Ocid
)

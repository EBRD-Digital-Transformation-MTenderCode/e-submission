package com.procurement.submission.application.model.data.bid.status

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.ProcurementMethod

data class FinalBidsStatusByLotsContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethod
)

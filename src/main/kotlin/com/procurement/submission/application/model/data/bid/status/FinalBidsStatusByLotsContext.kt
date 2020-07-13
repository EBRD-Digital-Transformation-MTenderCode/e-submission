package com.procurement.submission.application.model.data.bid.status

import com.procurement.submission.domain.model.enums.ProcurementMethod

data class FinalBidsStatusByLotsContext(
    val cpid: String,
    val pmd: ProcurementMethod
)

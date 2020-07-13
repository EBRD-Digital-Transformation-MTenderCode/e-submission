package com.procurement.submission.application.service

import com.procurement.submission.domain.model.enums.ProcurementMethod

data class FinalBidsStatusByLotsContext(
    val cpid: String,
    val pmd: ProcurementMethod
)

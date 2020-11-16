package com.procurement.submission.application.model.data.bid.get

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.enums.ProcurementMethod

data class GetBidsForEvaluationContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethod,
    val country: String
)

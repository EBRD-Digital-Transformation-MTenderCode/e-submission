package com.procurement.submission.application.model.data.award.apply

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

data class ApplyEvaluatedAwardsContext(
    val cpid: Cpid,
    val ocid: Ocid
)

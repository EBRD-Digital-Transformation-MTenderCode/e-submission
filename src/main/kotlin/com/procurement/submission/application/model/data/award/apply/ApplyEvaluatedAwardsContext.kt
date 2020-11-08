package com.procurement.submission.application.model.data.award.apply

import com.procurement.submission.domain.model.Cpid

data class ApplyEvaluatedAwardsContext(
    val cpid: Cpid,
    val stage: String
)

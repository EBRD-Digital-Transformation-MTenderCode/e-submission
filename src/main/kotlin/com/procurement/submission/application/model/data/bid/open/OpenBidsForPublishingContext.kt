package com.procurement.submission.application.model.data.bid.open

import com.procurement.submission.domain.model.Cpid

data class OpenBidsForPublishingContext(
    val cpid: Cpid,
    val stage: String
)

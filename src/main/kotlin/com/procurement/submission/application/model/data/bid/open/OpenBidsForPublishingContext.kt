package com.procurement.submission.application.model.data.bid.open

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

data class OpenBidsForPublishingContext(
    val cpid: Cpid,
    val ocid: Ocid
)

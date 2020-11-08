package com.procurement.submission.application.model.data.bid.document.open

import com.procurement.submission.domain.model.Cpid

class OpenBidDocsContext(
    val cpid: Cpid,
    val stage: String
)
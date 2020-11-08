package com.procurement.submission.application.model.data.bid.get.bylots

import com.procurement.submission.domain.model.Cpid

class GetBidsByLotsContext(
    val cpid: Cpid,
    val stage: String
)
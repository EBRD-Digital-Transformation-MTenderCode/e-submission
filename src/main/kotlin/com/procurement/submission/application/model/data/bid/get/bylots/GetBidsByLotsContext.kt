package com.procurement.submission.application.model.data.bid.get.bylots

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid

class GetBidsByLotsContext(
    val cpid: Cpid,
    val ocid: Ocid
)

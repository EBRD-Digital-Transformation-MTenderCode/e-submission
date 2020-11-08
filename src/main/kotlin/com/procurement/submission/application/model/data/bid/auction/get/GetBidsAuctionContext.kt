package com.procurement.submission.application.model.data.bid.auction.get

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.enums.ProcurementMethod

data class GetBidsAuctionContext(
    val cpid: Cpid,
    val stage: String,
    val pmd: ProcurementMethod,
    val country: String
)

package com.procurement.submission.model.entity

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Stage
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.model.dto.ocds.Bid
import java.util.*

data class BidEntityComplex(
    val cpid: Cpid,
    val bidId: BidId,
    val token: Token,
    val stage: Stage,
    val owner: Owner,
    var status: Status,
    val createdDate: Date,
    var pendingDate: Date?,
    var bid: Bid
)

package com.procurement.submission.model.entity

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.model.dto.ocds.Bid
import java.time.LocalDateTime

data class BidEntityComplex(
    val cpid: Cpid,
    val ocid: Ocid,
    val bidId: BidId,
    val token: Token,
    val owner: Owner,
    var status: Status,
    val createdDate: LocalDateTime,
    var pendingDate: LocalDateTime?,
    var bid: Bid
)

package com.procurement.submission.model.entity

import com.procurement.submission.model.dto.ocds.Bid
import java.util.*

data class BidEntityComplex(
    val cpid: String,
    val bidId: UUID,
    val token: UUID,
    val stage: String,
    val owner: String,
    var status: String,
    val createdDate: Date,
    var pendingDate: Date?,
    var jsonData: Bid
)

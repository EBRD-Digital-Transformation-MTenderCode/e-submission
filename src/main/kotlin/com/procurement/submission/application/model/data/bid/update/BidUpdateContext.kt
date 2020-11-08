package com.procurement.submission.application.model.data.bid.update

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import java.time.LocalDateTime
import java.util.*

data class BidUpdateContext(
    val id: String,
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: String,
    val stage: String,
    val token: UUID,
    val startDate: LocalDateTime
)

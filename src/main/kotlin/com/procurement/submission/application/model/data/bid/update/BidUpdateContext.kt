package com.procurement.submission.application.model.data.bid.update

import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import java.time.LocalDateTime
import java.util.*

data class BidUpdateContext(
    val id: String,
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: Owner,
    val token: UUID,
    val startDate: LocalDateTime
)

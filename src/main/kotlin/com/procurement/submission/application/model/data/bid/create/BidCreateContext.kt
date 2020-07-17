package com.procurement.submission.application.model.data.bid.create

import java.time.LocalDateTime

data class BidCreateContext(
    val cpid: String,
    val owner: String,
    val stage: String,
    val startDate: LocalDateTime
)

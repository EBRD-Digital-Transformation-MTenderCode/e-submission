package com.procurement.submission.application.service

import java.time.LocalDateTime

data class BidCreateContext(
    val cpid: String,
    val owner: String,
    val stage: String,
    val startDate: LocalDateTime
)

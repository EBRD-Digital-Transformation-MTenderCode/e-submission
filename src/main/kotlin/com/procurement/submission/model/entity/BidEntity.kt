package com.procurement.submission.model.entity

import java.util.*

data class BidEntity(

        val cpId: String,

        val bidId: UUID,

        val token: UUID,

        val stage: String,

        val owner: String,

        val status: String,

        val createdDate: Date,

        var pendingDate: Date?,

        var jsonData: String
)

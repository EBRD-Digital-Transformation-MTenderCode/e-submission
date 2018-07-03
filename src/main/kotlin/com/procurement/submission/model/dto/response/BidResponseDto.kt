package com.procurement.submission.model.dto.response

import com.procurement.submission.model.dto.ocds.Bid

data class BidResponseDto(

        val token: String,

        val bidId: String,

        val bid: Bid
)

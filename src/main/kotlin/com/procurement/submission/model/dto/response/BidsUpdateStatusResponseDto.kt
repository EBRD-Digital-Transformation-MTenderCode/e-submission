package com.procurement.submission.model.dto.response

import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Period

data class BidsUpdateStatusResponseDto(

        val tenderPeriod: Period,

        val bids: List<Bid>
)

package com.procurement.submission.model.dto.response

import com.procurement.submission.model.dto.ocds.Bids
import com.procurement.submission.model.dto.ocds.Period

data class BidsCopyResponseDto(

        val bids: Bids,

        val tenderPeriod: Period
)

package com.procurement.submission.model.dto.response

import com.procurement.submission.model.dto.ocds.Bid

data class BidsSelectionResponseDto(

        val bids: List<Bid>
)

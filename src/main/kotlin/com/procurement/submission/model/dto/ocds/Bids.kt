package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class Bids @JsonCreator constructor(
        val details: List<Bid>?
)
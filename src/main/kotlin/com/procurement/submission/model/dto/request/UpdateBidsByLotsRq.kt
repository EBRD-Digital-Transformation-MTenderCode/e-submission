package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateBidsByLotsRq @JsonCreator constructor(

        val firstBids: Set<FirstBid>?,

        val unsuccessfulLots: List<LotDto>?
)

data class FirstBid(val id: String)

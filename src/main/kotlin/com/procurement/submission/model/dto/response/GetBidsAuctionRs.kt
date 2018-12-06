package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetBidsAuctionRs @JsonCreator constructor(

        val bidsData: Set<BidsData>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsData @JsonCreator constructor(

        var owner: String,

        var bids: Set<BidDto>
)
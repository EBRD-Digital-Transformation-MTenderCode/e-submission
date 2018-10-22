package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Bid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetBidsRs @JsonCreator constructor(

        var bids: Set<BidDto>
)

package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Bid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetDocsOfConsideredBidRq @JsonCreator constructor(

        val consideredBidId: String
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetDocsOfConsideredBidRs @JsonCreator constructor(

        val consideredBid: Bid
)

package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Period

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsUpdateStatusRs @JsonCreator constructor(

        val tenderPeriod: Period,

        val bids: Set<Bid>
)

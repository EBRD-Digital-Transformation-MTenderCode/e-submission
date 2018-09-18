package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Bids
import com.procurement.submission.model.dto.ocds.Period

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsCopyRs @JsonCreator constructor(

        val bids: Bids,

        val tenderPeriod: Period
)

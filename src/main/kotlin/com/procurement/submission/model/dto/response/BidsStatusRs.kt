package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsStatusRs @JsonCreator constructor(

        val bids: List<FinalBid>
)

data class FinalBid @JsonCreator constructor(

        var id: String,

        var status: Status,

        var statusDetails: StatusDetails

)
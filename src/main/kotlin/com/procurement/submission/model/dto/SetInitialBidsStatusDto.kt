package com.procurement.submission.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails

data class SetInitialBidsStatusDtoRq @JsonCreator constructor(

        val awards: Set<SetInitialAward>
)

data class SetInitialAward @JsonCreator constructor(

        val relatedBid: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SetInitialBidsStatusDtoRs @JsonCreator constructor(

        val bids:  List<BidDetails>
)

data class BidDetails @JsonCreator constructor(

        var id: String,

        var status: Status,

        var statusDetails: StatusDetails

)

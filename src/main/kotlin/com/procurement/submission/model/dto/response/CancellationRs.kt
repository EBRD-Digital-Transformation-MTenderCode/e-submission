package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancellationRs @JsonCreator constructor(

        val bids: List<BidCancellation>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidCancellation @JsonCreator constructor(

        var id: String,

        var date: LocalDateTime,

        var status: Status,

        var statusDetails: StatusDetails
)

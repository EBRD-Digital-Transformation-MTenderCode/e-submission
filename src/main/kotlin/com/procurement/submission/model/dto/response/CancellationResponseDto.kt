package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancellationResponseDto @JsonCreator constructor(

        val bids: List<BidCancellation>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidCancellation @JsonCreator constructor(

        var id: String,

        var date: LocalDateTime,

        var status: Status,

        var statusDetails: StatusDetails
)

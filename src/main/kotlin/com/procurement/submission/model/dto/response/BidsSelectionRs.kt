package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Bid
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidsSelectionRs @JsonCreator constructor(

        @get:JsonProperty("isPeriodExpired")
        val isPeriodExpired: Boolean?,

        val tenderPeriodEndDate: LocalDateTime?,

        var bids: Set<Bid>
)

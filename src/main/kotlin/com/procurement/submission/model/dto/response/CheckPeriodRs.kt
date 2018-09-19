package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CheckPeriodRs @JsonCreator constructor(

        @get:JsonProperty("isPeriodExpired")
        val isPeriodExpired: Boolean?,

        @get:JsonProperty("setExtendedPeriod")
        val setExtendedPeriod: Boolean?,

        @get:JsonProperty("isPeriodChanged")
        val isPeriodChanged: Boolean?,

        val tenderPeriodEndDate: LocalDateTime?
)

package com.procurement.submission.infrastructure.api.v1.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime


@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CheckPeriodEndDateRs @JsonCreator constructor(

        @get:JsonProperty("isTenderPeriodExpired")
        val isTenderPeriodExpired: Boolean,

        val startDate: LocalDateTime,

        val endDate: LocalDateTime
)
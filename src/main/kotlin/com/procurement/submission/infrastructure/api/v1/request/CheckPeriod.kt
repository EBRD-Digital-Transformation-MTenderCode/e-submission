package com.procurement.submission.infrastructure.api.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Period
import java.time.LocalDateTime

data class CheckPeriodRq @JsonCreator constructor(

        @get:JsonProperty("setExtendedPeriod")
        val setExtendedPeriod: Boolean?,

        @get:JsonProperty("isEnquiryPeriodChanged")
        val isEnquiryPeriodChanged: Boolean?,

        val enquiryPeriod: Period,

        val tenderPeriod: Period
)

data class CheckPeriodRs @JsonCreator constructor(

        @get:JsonProperty("isTenderPeriodChanged")
        val isTenderPeriodChanged: Boolean?,

        val startDate: LocalDateTime,

        val endDate: LocalDateTime
)

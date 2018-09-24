package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Period
import javax.validation.Valid

data class CheckPeriodOTRq @JsonCreator constructor(

        @get:JsonProperty("setExtendedPeriod")
        val setExtendedPeriod: Boolean?,

        @get:JsonProperty("isEnquiryPeriodChanged")
        val isEnquiryPeriodChanged: Boolean?,

        @field:Valid
        val enquiryPeriod: Period,

        @field:Valid
        val tenderPeriod: Period
)

data class CheckPeriodOTRs @JsonCreator constructor(

        @get:JsonProperty("isTenderPeriodChanged")
        val isTenderPeriodChanged: Boolean?,

        val tenderPeriod: Period?
)

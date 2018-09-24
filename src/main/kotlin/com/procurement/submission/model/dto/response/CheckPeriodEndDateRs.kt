package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.model.dto.ocds.Period


@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CheckPeriodEndDateRs @JsonCreator constructor(

        @get:JsonProperty("isTenderPeriodExpired")
        val isTenderPeriodExpired: Boolean?,

        val tender: Tender?
)

data class Tender(

        val tenderPeriod: Period
)
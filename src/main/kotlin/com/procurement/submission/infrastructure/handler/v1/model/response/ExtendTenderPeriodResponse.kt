package com.procurement.submission.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ExtendTenderPeriodResponse(
    @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod
) {
    data class TenderPeriod(
        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
        @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
    )
}

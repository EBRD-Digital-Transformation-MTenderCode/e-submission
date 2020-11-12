package com.procurement.submission.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class SetTenderPeriodResult(
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod
    ) {
        data class TenderPeriod(
            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,
            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
        )
    }
}
package com.procurement.submission.infrastructure.dto.tender.period.set


import com.fasterxml.jackson.annotation.JsonProperty

data class SetTenderPeriodRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @param:JsonProperty("tenderPeriod") @field:JsonProperty("tenderPeriod") val tenderPeriod: TenderPeriod
    ) {
        data class TenderPeriod(
            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String
        )
    }
}
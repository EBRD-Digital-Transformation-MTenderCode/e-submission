package com.procurement.submission.infrastructure.dto.tender.period


import com.fasterxml.jackson.annotation.JsonProperty

data class ValidateTenderPeriodRequest(
    @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
    @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
    @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: String,
    @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: String,
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
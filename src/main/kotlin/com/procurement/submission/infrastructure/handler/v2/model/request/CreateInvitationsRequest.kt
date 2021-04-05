package com.procurement.submission.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class CreateInvitationsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("additionalCpid") @field:JsonProperty("additionalCpid") val additionalCpid: String,
    @param:JsonProperty("additionalOcid") @field:JsonProperty("additionalOcid") val additionalOcid: String,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: String,
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}
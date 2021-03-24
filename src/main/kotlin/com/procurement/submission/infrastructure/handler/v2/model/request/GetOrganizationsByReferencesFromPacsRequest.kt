package com.procurement.submission.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class GetOrganizationsByReferencesFromPacsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("parties") @field:JsonProperty("parties") val parties: List<Party>
) {
    data class Party(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
    )
}
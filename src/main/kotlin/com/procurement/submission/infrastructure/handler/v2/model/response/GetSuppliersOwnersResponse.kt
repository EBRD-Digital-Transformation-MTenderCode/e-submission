package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GetSuppliersOwnersResponse(
    @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>
) {
    data class Tenderer(
        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
        @param:JsonProperty("organizations") @field:JsonProperty("organizations") val organizations: List<Organization>
    ) {
        data class Organization(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        )
    }
}
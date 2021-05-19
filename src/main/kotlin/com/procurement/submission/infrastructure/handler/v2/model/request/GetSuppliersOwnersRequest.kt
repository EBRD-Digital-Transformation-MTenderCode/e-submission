package com.procurement.submission.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class GetSuppliersOwnersRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("contracts") @field:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("suppliers") @field:JsonProperty("suppliers") val suppliers: List<Supplier>
    ) {
        data class Supplier(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}
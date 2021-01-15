package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty

data class OrganizationReference(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
)
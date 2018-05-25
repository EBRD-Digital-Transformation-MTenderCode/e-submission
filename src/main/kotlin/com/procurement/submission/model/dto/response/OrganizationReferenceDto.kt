package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class OrganizationReferenceDto(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("name") @NotNull
        val name: String
)


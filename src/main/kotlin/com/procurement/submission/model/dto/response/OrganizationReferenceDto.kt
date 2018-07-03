package com.procurement.submission.model.dto.response

import javax.validation.constraints.NotNull

data class OrganizationReferenceDto(

        @field:NotNull
        val id: String,

        @field:NotNull
        val name: String
)


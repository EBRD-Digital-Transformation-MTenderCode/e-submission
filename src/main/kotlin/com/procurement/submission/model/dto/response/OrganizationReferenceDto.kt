package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class OrganizationReferenceDto @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val name: String
)


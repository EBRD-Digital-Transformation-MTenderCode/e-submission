package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReference @JsonCreator constructor(

        var id: String?,

        @field:NotNull
        val name: String,

        @field:Valid @field:NotNull
        val identifier: Identifier,

        @field:Valid @field:NotNull
        val address: Address,

        @field:Valid
        val additionalIdentifiers: Set<Identifier>?,

        @field:Valid @field:NotNull
        val contactPoint: ContactPoint
)

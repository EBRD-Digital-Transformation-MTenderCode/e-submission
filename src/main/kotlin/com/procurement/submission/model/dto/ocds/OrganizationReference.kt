package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReference @JsonCreator constructor(

        var id: String?,

        val name: String,

        val identifier: Identifier,

        val address: Address,

        val additionalIdentifiers: Set<Identifier>?,

        val contactPoint: ContactPoint,

        var details: Details,

        val persones: List<Persone>
)

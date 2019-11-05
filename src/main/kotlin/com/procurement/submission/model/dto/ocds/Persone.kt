package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class Persone  @JsonCreator constructor(
    val title: String,
    val name: String,
    val identifier: Identifier,
    val businessFunctions: List<BusinessFunction>
) {
    data class Identifier  @JsonCreator constructor(
        val id: String,
        val scheme: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val uri: String?
    )
}
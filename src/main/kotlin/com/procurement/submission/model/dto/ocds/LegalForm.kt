package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class LegalForm @JsonCreator constructor(
    val scheme: String,
    val id: String,
    val description: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val uri: String?
)
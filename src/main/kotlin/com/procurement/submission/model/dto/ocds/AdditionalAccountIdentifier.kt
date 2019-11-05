package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class AdditionalAccountIdentifier @JsonCreator constructor(
    val id: String,
    val scheme: String
)
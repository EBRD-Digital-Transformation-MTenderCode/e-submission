package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class LegalForm @JsonCreator constructor(
    val scheme: String,
    val id: String,
    val description: String,
    val uri: String?
)
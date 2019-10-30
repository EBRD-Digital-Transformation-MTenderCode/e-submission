package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class Permit @JsonCreator constructor(
    val id: String,
    val scheme: String,
    val url: String?,
    val permitDetails: PermitDetails
)
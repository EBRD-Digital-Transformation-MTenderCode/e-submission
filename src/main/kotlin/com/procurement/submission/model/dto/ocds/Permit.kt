package com.procurement.submission.model.dto.ocds

data class Permit(
    val id: String,
    val scheme: String,
    val url: String?,
    val permitDetails: PermitDetails
)
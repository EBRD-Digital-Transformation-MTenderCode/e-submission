package com.procurement.submission.model.dto.ocds

data class LegalForm(
    val scheme: String,
    val id: String,
    val description: String,
    val uri: String?
)
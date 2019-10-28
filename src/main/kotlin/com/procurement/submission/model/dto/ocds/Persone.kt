package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class Persone(
    val title: String,
    val name: String,
    val identifier: Identifier,
    val businessFunctions: List<BusinessFunction>
) {
    data class Identifier (
        val id: String,
        val scheme: String,
        val uri: String?
    )
}
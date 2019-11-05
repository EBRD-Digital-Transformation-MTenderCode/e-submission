package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class IssuedThought @JsonCreator constructor(
    val id: String,
    val name: String
)
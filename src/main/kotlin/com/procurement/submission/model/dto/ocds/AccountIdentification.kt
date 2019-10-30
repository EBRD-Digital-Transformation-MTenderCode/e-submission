package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class AccountIdentification @JsonCreator constructor(
    val scheme: String,
    val id: String
)
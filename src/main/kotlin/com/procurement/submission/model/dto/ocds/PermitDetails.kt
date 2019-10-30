package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class PermitDetails @JsonCreator constructor (
    val issuedBy: IssuedBy,
    val issuedThought: IssuedThought,
    val validityPeriod: ValidityPeriod
)
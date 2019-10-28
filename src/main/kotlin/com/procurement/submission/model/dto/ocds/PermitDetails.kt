package com.procurement.submission.model.dto.ocds

data class PermitDetails(
    val issuedBy: IssuedBy,
    val issuedThought: IssuedThought,
    val validityPeriod: ValidityPeriod
)
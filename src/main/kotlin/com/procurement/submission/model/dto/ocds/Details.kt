package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class Details @JsonCreator constructor(
        val typeOfSupplier: String,
        val mainEconomicActivities: List<String>,
        val scale: String,
        val permits: List<Permit>,
        val bankAccounts: List<BankAccount>,
        val legalForm: LegalForm?
)

package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class Details @JsonCreator constructor(

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val typeOfSupplier: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val mainEconomicActivities: List<MainEconomicActivity>?,
        val scale: String,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val permits: List<Permit>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val bankAccounts: List<BankAccount>?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val legalForm: LegalForm?
)

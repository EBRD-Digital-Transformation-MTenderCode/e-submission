package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class BankAccount(
    val description: String,
    val bankName: String,
    val address: Address,
    val identifier: Identifier,
    val accountIdentification: AccountIdentification,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
) {
    data class Identifier @JsonCreator constructor(
        val scheme: String,
        val id: String
    )
}
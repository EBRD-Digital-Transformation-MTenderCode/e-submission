package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class BankAccount(
    val description: String,
    val bankName: String,
    val address: Address,
    val identifier: Identifier,
    val accountIdentification: AccountIdentification,
    val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
) {
    data class Identifier @JsonCreator constructor(
        val scheme: String,
        val id: String
    )
}
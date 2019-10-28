package com.procurement.submission.model.dto.ocds

data class BankAccount(
    val description: String,
    val bankName: String,
    val address: Address,
    val identifier: Identifier,
    val accountIdentification: AccountIdentification,
    val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
) {
    data class Identifier(
        val scheme: String,
        val id: String
    )
}
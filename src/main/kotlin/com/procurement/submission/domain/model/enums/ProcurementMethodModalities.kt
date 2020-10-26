package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ProcurementMethodModalities(@JsonValue override val key: String) : EnumElementProvider.Key {
    REQUIRES_ELECTRONIC_CATALOGUE("requiresElectronicCatalogue"),
    ELECTRONIC_AUCTION("electronicAuction");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodModalities>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TypeOfSupplier.orThrow(name)
    }
}
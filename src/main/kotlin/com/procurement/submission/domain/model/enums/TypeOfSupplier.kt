package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class TypeOfSupplier(@JsonValue override val key: String) : EnumElementProvider.Key {
    COMPANY("company"),
    INDIVIDUAL("individual");

    override fun toString(): String = key

    companion object : EnumElementProvider<TypeOfSupplier>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TypeOfSupplier.orThrow(name)
    }
}
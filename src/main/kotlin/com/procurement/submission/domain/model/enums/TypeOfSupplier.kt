package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException

enum class TypeOfSupplier(@JsonValue val value: String) {
    COMPANY("company"),
    INDIVIDUAL("individual");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, TypeOfSupplier> = values().associateBy { it.value.toUpperCase() }

        @JvmStatic
        @JsonCreator
        fun fromString(value: String): TypeOfSupplier = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = TypeOfSupplier::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}

package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException

enum class Scale(@JsonValue val value: String) {
    MICRO("micro"),
    SME("sme"),
    LARGE("large"),
    EMPTY("");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, Scale> =
            values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): Scale = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = Scale::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
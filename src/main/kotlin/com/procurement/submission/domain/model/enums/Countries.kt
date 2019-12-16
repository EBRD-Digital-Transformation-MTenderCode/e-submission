package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException

enum class Countries(@JsonValue val value: String) {
    MD("MD");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, Countries> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): Countries = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = Countries::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
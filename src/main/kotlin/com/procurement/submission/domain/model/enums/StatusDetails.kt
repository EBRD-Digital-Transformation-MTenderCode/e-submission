package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException

enum class StatusDetails(@JsonValue val value: String) {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty"),
    ARCHIVED("archived");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, StatusDetails> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): StatusDetails = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = StatusDetails::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
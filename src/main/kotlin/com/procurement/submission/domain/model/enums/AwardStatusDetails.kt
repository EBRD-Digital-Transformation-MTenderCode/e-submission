package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException

enum class AwardStatusDetails(@JsonValue val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, AwardStatusDetails> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): AwardStatusDetails = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = AwardStatusDetails::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
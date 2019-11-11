package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException

enum class Status(@JsonValue val value: String) {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, Status> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): Status = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = Status::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
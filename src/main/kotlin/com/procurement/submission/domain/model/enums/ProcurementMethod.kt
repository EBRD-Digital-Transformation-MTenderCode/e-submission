package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.application.exception.EnumException

enum class ProcurementMethod(@JsonValue val key: String) {
    MV("open"),
    OT("open"),
    RT("selective"),
    SV("open"),
    DA("limited"),
    NP("limited"),
    FA("limited"),
    OP("selective"),
    GPA("selective"),
    TEST_OT("open"),
    TEST_SV("open"),
    TEST_RT("selective"),
    TEST_MV("open"),
    TEST_DA("limited"),
    TEST_NP("limited"),
    TEST_FA("limited"),
    TEST_OP("selective"),
    TEST_GPA("selective");

    override fun toString(): String {
        return this.key
    }

    companion object {

        val allowedValues = values()

        fun fromString(name: String): ProcurementMethod = try {
            valueOf(name.toUpperCase())
        } catch (exception: Exception) {
            throw EnumException(
                enumType = ProcurementMethod::class.java.name,
                value = name,
                values = values().toString()
            )
        }

        fun orNull(name: String): ProcurementMethod? = try {
            allowedValues.find { it == valueOf(name) }
        } catch (ignored: Exception) {
            null
        }
    }
}

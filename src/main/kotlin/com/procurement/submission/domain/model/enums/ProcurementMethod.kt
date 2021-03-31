package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.application.exception.EnumException

enum class ProcurementMethod(@JsonValue val key: String) {
    CD("selective"),
    CF("selective"),
    DA("limited"),
    DC("selective"),
    FA("limited"),
    GPA("selective"),
    IP("selective"),
    MV("open"),
    NP("limited"),
    OF("selective"),
    OP("selective"),
    OT("open"),
    RFQ("open"),
    RT("selective"),
    SV("open"),
    TEST_CD("selective"),
    TEST_CF("selective"),
    TEST_DA("limited"),
    TEST_DC("selective"),
    TEST_FA("limited"),
    TEST_GPA("selective"),
    TEST_IP("selective"),
    TEST_MV("open"),
    TEST_NP("limited"),
    TEST_OF("selective"),
    TEST_OP("selective"),
    TEST_OT("open"),
    TEST_RFQ("open"),
    TEST_RT("selective"),
    TEST_SV("open");

    override fun toString(): String {
        return this.key
    }

    companion object {
        val elements  = values()
            .associateBy {
                it.name.toUpperCase()
            }

        fun fromString(name: String): ProcurementMethod = try {
            valueOf(name.toUpperCase())
        } catch (exception: Exception) {
            throw EnumException(
                enumType = ProcurementMethod::class.java.name,
                value = name,
                values = values().toString()
            )
        }

        fun orNull(name: String): ProcurementMethod? = elements[name.toUpperCase()]
    }
}

package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class BusinessFunctionType(@JsonValue override val key: String) : EnumElementProvider.Key {
    AUTHORITY("authority"),
    CONTACT_POINT("contactPoint");

    override fun toString(): String = key

    companion object : EnumElementProvider<BusinessFunctionType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = BusinessFunctionType.orThrow(name)
    }
}
package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AwardStatus(@JsonValue override val key: String) : EnumElementProvider.Key {
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardStatus>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardStatus.orThrow(name)
    }
}
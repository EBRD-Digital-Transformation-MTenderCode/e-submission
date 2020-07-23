package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Countries(@JsonValue override val key: String) : EnumElementProvider.Key {
    MD("MD");

    override fun toString(): String = key

    companion object : EnumElementProvider<Countries>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Countries.orThrow(name)
    }
}
package com.procurement.submission.infrastructure.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.domain.model.enums.EnumElementProvider
import com.procurement.submission.domain.Action

enum class Command2Type(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    TODO("todo");

    override fun toString(): String = key

    companion object : EnumElementProvider<Command2Type>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Command2Type.orThrow(name)
    }
}





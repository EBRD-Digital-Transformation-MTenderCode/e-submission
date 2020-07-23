package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {

    START_SECOND_STAGE("startSecondStage");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}

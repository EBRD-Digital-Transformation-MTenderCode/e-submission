package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class BidStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty"),
    ARCHIVED("archived");

    override fun toString(): String = key

    companion object : EnumElementProvider<BidStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = BidStatusDetails.orThrow(name)
    }
}
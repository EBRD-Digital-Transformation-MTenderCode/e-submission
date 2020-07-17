package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AwardStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    AWAITING("awaiting"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    LOT_CANCELLED("lotCancelled");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardStatusDetails.orThrow(name)
    }
}
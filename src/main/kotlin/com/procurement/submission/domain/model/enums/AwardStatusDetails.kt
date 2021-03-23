package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AwardStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    ACTIVE("active"),
    AWAITING("awaiting"),
    BASED_ON_HUMAN_DECISION("basedOnHumanDecision"),
    CONSIDERATION("consideration"),
    EMPTY("empty"),
    LOT_CANCELLED("lotCancelled"),
    NO_OFFERS_RECEIVED("noOffersReceived"),
    PENDING("pending"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardStatusDetails.orThrow(name)
    }
}
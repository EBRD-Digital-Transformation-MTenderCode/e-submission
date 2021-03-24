package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class PartyRole(@JsonValue override val key: String) : EnumElementProvider.Key {

    AUTHOR("author"),
    BUYER("buyer"),
    CANDIDATE("candidate"),
    CLIENT("client"),
    ENQUIRER("enquirer"),
    FUNDER("funder"),
    INVITED_CANDIDATE("invitedCandidate"),
    INVITED_TENDERER("invitedTenderer"),
    PAYEE("payee"),
    PAYER("payer"),
    PROCURING_ENTITY("procuringEntity"),
    REVIEW_BODY("reviewBody"),
    SUPPLIER("supplier"),
    TENDERER("tenderer");

    override fun toString(): String = key

    companion object : EnumElementProvider<PartyRole>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}

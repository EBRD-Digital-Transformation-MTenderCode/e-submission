package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {

    COMPLETE_QUALIFICATION("completeQualification"),
    CREATE_PCR("createPcr"),
    CREATE_RFQ("createRfq"),
    QUALIFICATION_PROTOCOL("qualificationProtocol"),
    START_SECOND_STAGE("startSecondStage"),
    SUBMIT_BID_IN_PCR("submitBidInPcr"),
    WITHDRAW_BID("withdrawBid");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}

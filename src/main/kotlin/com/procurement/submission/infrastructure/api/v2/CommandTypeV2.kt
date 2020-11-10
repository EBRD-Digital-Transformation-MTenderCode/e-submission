package com.procurement.submission.infrastructure.api.v2

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.domain.model.enums.EnumElementProvider
import com.procurement.submission.infrastructure.api.Action

enum class CommandTypeV2(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    CHECK_ABSENCE_ACTIVE_INVITATIONS("checkAbsenceActiveInvitations"),
    CHECK_PERIOD("checkPeriod"),
    CREATE_BID("createBid"),
    DO_INVITATIONS("doInvitations"),
    SET_TENDER_PERIOD("setTenderPeriod"),
    VALIDATE_BID_DATA("validateBidData"),
    VALIDATE_TENDER_PERIOD("validateTenderPeriod"),
    PUBLISH_INVITATIONS("publishInvitations");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandTypeV2>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CommandTypeV2.orThrow(name)
    }
}





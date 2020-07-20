package com.procurement.submission.infrastructure.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.domain.Action
import com.procurement.submission.domain.model.enums.EnumElementProvider

enum class Command2Type(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    CHECK_ABSENCE_ACTIVE_INVITATIONS("checkAbsenceActiveInvitations"),
    DO_INVITATIONS("doInvitations"),
    VALIDATE_TENDER_PERIOD("validateTenderPeriod");

    override fun toString(): String = key

    companion object : EnumElementProvider<Command2Type>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Command2Type.orThrow(name)
    }
}





package com.procurement.submission.infrastructure.dto.invitation.check

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAbsenceActiveInvitationsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String
)

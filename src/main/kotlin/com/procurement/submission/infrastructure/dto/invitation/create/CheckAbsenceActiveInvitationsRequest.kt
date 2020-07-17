package com.procurement.submission.infrastructure.dto.invitation.create

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAbsenceActiveInvitationsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String
)

package com.procurement.submission.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAbsenceActiveInvitationsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String
)

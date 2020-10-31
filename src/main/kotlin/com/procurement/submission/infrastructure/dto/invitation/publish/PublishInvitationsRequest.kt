package com.procurement.submission.infrastructure.dto.invitation.publish

import com.fasterxml.jackson.annotation.JsonProperty

data class PublishInvitationsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: String

    )
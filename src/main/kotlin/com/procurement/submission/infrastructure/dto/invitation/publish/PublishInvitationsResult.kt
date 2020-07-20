package com.procurement.submission.infrastructure.dto.invitation.publish

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.invitation.InvitationId

data class PublishInvitationsResult(
    @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
) {
    data class Invitation(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus
    )
}

fun Invitation.convert(): PublishInvitationsResult.Invitation =
    PublishInvitationsResult.Invitation(
        id = this.id,
        status = this.status
    )
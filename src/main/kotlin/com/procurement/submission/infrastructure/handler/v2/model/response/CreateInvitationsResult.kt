package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.invitation.InvitationId
import java.io.Serializable
import java.time.LocalDateTime

data class CreateInvitationsResult(
    @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
) {
    data class Invitation(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus,
        @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>
    ) : Serializable { companion object {}

        data class Tenderer(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        ) : Serializable
    }
}

fun CreateInvitationsResult.Invitation.Companion.fromDomain(invitation: Invitation): CreateInvitationsResult.Invitation {
    return CreateInvitationsResult.Invitation(
        id = invitation.id,
        date = invitation.date,
        status = invitation.status,
        tenderers = invitation.tenderers.map { tenderer ->
            CreateInvitationsResult.Invitation.Tenderer(
                id = tenderer.id,
                name = tenderer.name
            )
        }
    )
}
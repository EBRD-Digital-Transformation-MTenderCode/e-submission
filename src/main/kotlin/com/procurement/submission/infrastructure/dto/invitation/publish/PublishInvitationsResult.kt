package com.procurement.submission.infrastructure.dto.invitation.publish

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.Invitation
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.qualification.QualificationId
import java.time.LocalDateTime

data class PublishInvitationsResult(
    @param:JsonProperty("invitations") @field:JsonProperty("invitations") val invitations: List<Invitation>
) {
    data class Invitation(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus,
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
        @param:JsonProperty("relatedQualification") @field:JsonProperty("relatedQualification") val relatedQualification: QualificationId,
        @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderers>
    ){
        data class Tenderers(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
        )
    }
}

fun Invitation.convert(): PublishInvitationsResult.Invitation =
    PublishInvitationsResult.Invitation(
        id = id,
        status = status,
        date = date,
        relatedQualification = relatedQualification,
        tenderers = tenderers.map { tenderer ->
            PublishInvitationsResult.Invitation.Tenderers(
                id = tenderer.id,
                name = tenderer.name
            )
        }
    )
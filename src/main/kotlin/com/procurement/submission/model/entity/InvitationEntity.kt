package com.procurement.submission.model.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.invitation.InvitationId
import com.procurement.submission.domain.model.qualification.QualificationId
import java.time.LocalDateTime

data class InvitationEntity(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: InvitationId,
    @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,
    @param:JsonProperty("status") @field:JsonProperty("status") val status: InvitationStatus,
    @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,
    @param:JsonProperty("relatedQualification") @field:JsonProperty("relatedQualification") val relatedQualification: QualificationId
) {
    data class Tenderer(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
    )
}

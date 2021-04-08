package com.procurement.submission.domain.model.invitation

import com.procurement.submission.domain.model.enums.InvitationStatus
import com.procurement.submission.domain.model.qualification.QualificationId
import java.time.LocalDateTime

data class Invitation(
    val id: InvitationId,
    val date: LocalDateTime,
    val status: InvitationStatus,
    val tenderers: List<Tenderer>,
    val relatedQualification: QualificationId?
) {
    data class Tenderer(
        val id: String,
        val name: String
    )
}
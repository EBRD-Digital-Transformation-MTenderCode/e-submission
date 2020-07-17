package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.infrastructure.dto.invitation.create.CheckAbsenceActiveInvitationsRequest

fun CheckAbsenceActiveInvitationsRequest.convert() = CheckAbsenceActiveInvitationsParams.tryCreate(cpid = cpid)

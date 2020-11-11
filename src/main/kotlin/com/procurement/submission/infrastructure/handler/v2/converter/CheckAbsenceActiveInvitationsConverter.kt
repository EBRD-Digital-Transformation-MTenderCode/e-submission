package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.CheckAbsenceActiveInvitationsParams
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckAbsenceActiveInvitationsRequest

fun CheckAbsenceActiveInvitationsRequest.convert() = CheckAbsenceActiveInvitationsParams.tryCreate(cpid = cpid)

package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.infrastructure.dto.invitation.publish.PublishInvitationsRequest

fun PublishInvitationsRequest.convert() = PublishInvitationsParams.tryCreate(cpid = cpid)

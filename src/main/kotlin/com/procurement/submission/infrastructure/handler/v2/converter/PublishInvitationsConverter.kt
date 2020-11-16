package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.PublishInvitationsParams
import com.procurement.submission.infrastructure.handler.v2.model.request.PublishInvitationsRequest

fun PublishInvitationsRequest.convert() = PublishInvitationsParams.tryCreate(cpid = cpid, operationType = operationType)

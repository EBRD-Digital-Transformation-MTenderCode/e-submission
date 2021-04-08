package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.CreateInvitationsParams
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.request.CreateInvitationsRequest
import com.procurement.submission.lib.functional.Result

fun CreateInvitationsRequest.convert(): Result<CreateInvitationsParams, Fail> {
    val convertedTender = tender.convert().onFailure { return it }
    return CreateInvitationsParams.tryCreate(
        cpid = cpid,
        additionalCpid = additionalCpid, additionalOcid = additionalOcid,
        tender = convertedTender,
        date = date
    )
}

fun CreateInvitationsRequest.Tender.convert(): Result<CreateInvitationsParams.Tender, Fail> {
    val convertedLots = lots.mapResult { it.convert() }
        .onFailure { return it }
    return CreateInvitationsParams.Tender.tryCreate(lots = convertedLots)
}

fun CreateInvitationsRequest.Tender.Lot.convert(): Result<CreateInvitationsParams.Tender.Lot, Fail.Incident.Transform.Parsing> =
    CreateInvitationsParams.Tender.Lot.tryCreate(id)



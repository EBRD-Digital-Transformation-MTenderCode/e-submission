package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.CheckExistenceOfInvitationParams
import com.procurement.submission.application.params.parseBidId
import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckExistenceOfInvitationRequest
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate

fun CheckExistenceOfInvitationRequest.convert(): Result<CheckExistenceOfInvitationParams, DataErrors> = CheckExistenceOfInvitationParams(
    cpid = parseCpid(cpid).onFailure { return it },
    bids = this.bids.let { bid ->
        CheckExistenceOfInvitationParams.Bids(
            details = bid.details.mapResult { it.convert() }.onFailure { return it }
        )
    }
).asSuccess()

private fun CheckExistenceOfInvitationRequest.Bids.Detail.convert(): Result<CheckExistenceOfInvitationParams.Bids.Detail, DataErrors>
= CheckExistenceOfInvitationParams.Bids.Detail(
    id = parseBidId(id, "bids.detail.id").onFailure { return it },
    tenderers = tenderers
        .validate(notEmptyRule("bids.detail.tenderers"))
        .onFailure { return it }
        .map { tenderer -> CheckExistenceOfInvitationParams.Bids.Detail.Tenderer(tenderer.id) }
).asSuccess()
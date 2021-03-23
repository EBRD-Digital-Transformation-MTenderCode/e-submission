package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.params.bid.FinalizeBidsByAwardsParams
import com.procurement.submission.domain.extension.mapResult
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.handler.v2.model.request.FinalizeBidsByAwardsRequest
import com.procurement.submission.lib.functional.Result

fun FinalizeBidsByAwardsRequest.convert(): Result<FinalizeBidsByAwardsParams, Fail> {
    val awards = awards.mapResult { it.convert() }
        .onFailure { return it }

    return FinalizeBidsByAwardsParams.tryCreate(cpid = cpid, ocid = ocid, awards = awards)
}

private fun FinalizeBidsByAwardsRequest.Award.convert(): Result<FinalizeBidsByAwardsParams.Award, Fail> =
    FinalizeBidsByAwardsParams.Award.tryCreate(
        id = id,
        status = status,
        statusDetails = statusDetails,
        relatedBid = relatedBid
    )

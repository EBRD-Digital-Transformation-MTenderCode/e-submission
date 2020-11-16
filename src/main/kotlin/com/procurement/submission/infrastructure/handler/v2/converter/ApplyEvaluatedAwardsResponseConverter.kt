package com.procurement.submission.infrastructure.handler.v2.converter

import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsResult
import com.procurement.submission.infrastructure.handler.v2.model.response.ApplyEvaluatedAwardsResponse

fun ApplyEvaluatedAwardsResult.convert() = ApplyEvaluatedAwardsResponse(
    bids = this.bids
        .map { bid ->
            ApplyEvaluatedAwardsResponse.Bid(
                id = bid.id,
                statusDetails = bid.statusDetails
            )
        }
)

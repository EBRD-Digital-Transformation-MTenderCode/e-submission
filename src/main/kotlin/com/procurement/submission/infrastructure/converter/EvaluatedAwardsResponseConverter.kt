package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.service.ApplyEvaluatedAwardsResult
import com.procurement.submission.infrastructure.dto.award.EvaluatedAwardsResponse

fun ApplyEvaluatedAwardsResult.convert() = EvaluatedAwardsResponse(
    bids = this.bids
        .map { bid ->
            EvaluatedAwardsResponse.Bid(
                id = bid.id,
                statusDetails = bid.statusDetails
            )
        }
)

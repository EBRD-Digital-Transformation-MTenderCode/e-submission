package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.service.ApplyEvaluatedAwardsResult
import com.procurement.submission.infrastructure.dto.award.ApplyEvaluatedAwardsResponse

fun ApplyEvaluatedAwardsResult.convert() = ApplyEvaluatedAwardsResponse(
    bids = this.bids
        .map { bid ->
            ApplyEvaluatedAwardsResponse.Bid(
                id = bid.id,
                statusDetails = bid.statusDetails
            )
        }
)

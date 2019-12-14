package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.service.AppliedEvaluatedAwardsData
import com.procurement.submission.infrastructure.dto.award.EvaluatedAwardsResponse

fun AppliedEvaluatedAwardsData.convert() = EvaluatedAwardsResponse(
    bids = this.bids
        .map { bid ->
            EvaluatedAwardsResponse.Bid(
                id = bid.id,
                statusDetails = bid.statusDetails
            )
        }
)

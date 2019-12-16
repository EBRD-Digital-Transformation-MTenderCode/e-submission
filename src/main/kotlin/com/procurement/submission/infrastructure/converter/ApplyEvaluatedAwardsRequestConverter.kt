package com.procurement.submission.infrastructure.converter

import com.procurement.submission.application.service.ApplyEvaluatedAwardsData
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.infrastructure.dto.award.ApplyEvaluatedAwardsRequest
import com.procurement.submission.lib.mapIfNotEmpty
import com.procurement.submission.lib.orThrow

fun ApplyEvaluatedAwardsRequest.convert() = ApplyEvaluatedAwardsData(
    awards = this.awards
        .mapIfNotEmpty { award ->
            ApplyEvaluatedAwardsData.Award(
                statusDetails = award.statusDetails,
                relatedBid = award.relatedBid
            )
        }
        .orThrow {
            throw ErrorException(
                error = ErrorType.EMPTY_LIST,
                message = "The data contains empty list of the awards."
            )
        }
)

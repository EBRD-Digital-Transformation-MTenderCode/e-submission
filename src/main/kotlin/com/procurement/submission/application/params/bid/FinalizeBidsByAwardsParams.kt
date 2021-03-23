package com.procurement.submission.application.params.bid

import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.tryUUID
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.award.AwardId
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.AwardStatus
import com.procurement.submission.domain.model.enums.AwardStatusDetails
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate

class FinalizeBidsByAwardsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val awards: List<Award>
) {
    companion object {
        fun tryCreate(cpid: String, ocid: String, awards: List<Award>): Result<FinalizeBidsByAwardsParams, DataErrors> {
            val parsedCpid = parseCpid(cpid).onFailure { return it }
            val parsedOcid = parseOcid(ocid).onFailure { return it }

            awards.validate(notEmptyRule("awards"))
                .onFailure { fail -> return fail }

            return FinalizeBidsByAwardsParams(cpid = parsedCpid, ocid = parsedOcid, awards = awards).asSuccess()
        }
    }

    class Award private constructor(
        val id: AwardId,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedBid: BidId
    ) {
        companion object {
            private val allowedStatuses = AwardStatus.allowedElements
                .filter {
                    when (it) {
                        AwardStatus.ACTIVE,
                        AwardStatus.UNSUCCESSFUL -> true
                    }
                }.toSet()

            private val allowedStatusesDetails = AwardStatusDetails.allowedElements
                .filter {
                    when (it) {
                        AwardStatusDetails.BASED_ON_HUMAN_DECISION -> true
                        AwardStatusDetails.PENDING,
                        AwardStatusDetails.ACTIVE,
                        AwardStatusDetails.UNSUCCESSFUL,
                        AwardStatusDetails.CONSIDERATION,
                        AwardStatusDetails.EMPTY,
                        AwardStatusDetails.AWAITING,
                        AwardStatusDetails.NO_OFFERS_RECEIVED,
                        AwardStatusDetails.LOT_CANCELLED -> false
                    }
                }.toSet()

            fun tryCreate(
                id: String,
                status: String,
                statusDetails: String,
                relatedBid: String
            ): Result<Award, Fail> {

                val parsedId = id.tryUUID().onFailure { return it }
                val parsedRelatedBidId = relatedBid.tryUUID().onFailure { return it }

                val parsedStatus = AwardStatus.orNull(status)
                    ?: return DataErrors.Validation.UnknownValue(
                        name = "status",
                        expectedValues = allowedStatuses.map { it.key },
                        actualValue = status
                    ).asFailure()

                val parsedStatusDetail = AwardStatusDetails.orNull(statusDetails)
                    ?: return DataErrors.Validation.UnknownValue(
                        name = "statusDetails",
                        expectedValues = allowedStatusesDetails.map { it.key },
                        actualValue = status
                    ).asFailure()

                return Award(
                    id = parsedId,
                    status = parsedStatus,
                    statusDetails = parsedStatusDetail,
                    relatedBid = parsedRelatedBidId
                ).asSuccess()
            }
        }

    }
}
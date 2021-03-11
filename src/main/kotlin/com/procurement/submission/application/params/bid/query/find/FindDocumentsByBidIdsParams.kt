package com.procurement.submission.application.params.bid.query.find

import com.procurement.submission.application.params.parseCpid
import com.procurement.submission.application.params.parseOcid
import com.procurement.submission.application.params.rules.notEmptyRule
import com.procurement.submission.domain.extension.UUID_PATTERN
import com.procurement.submission.domain.fail.error.DataErrors
import com.procurement.submission.domain.fail.error.DataErrors.Validation.DataMismatchToPattern
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.lib.functional.validate

class FindDocumentsByBidIdsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val bids: Bids
) {
    companion object {
        private const val BIDS_ATTRIBUTE_NAME = "bids.details"

        fun tryCreate(cpid: String, ocid: String, bids: Bids): Result<FindDocumentsByBidIdsParams, DataErrors> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }

            bids.details
                .validate(notEmptyRule(BIDS_ATTRIBUTE_NAME))
                .onFailure { return it }

            return FindDocumentsByBidIdsParams(cpid = parsedCpid, ocid = parsedOcid, bids = bids).asSuccess()
        }
    }

    data class Bids(
        val details: List<BidDetails>
    ) {
        class BidDetails private constructor(
            val id: BidId
        ) {
            companion object {
                fun tryCreate(id: String): Result<BidDetails, DataMismatchToPattern> {
                    val parsedId = try { BidId.fromString(id) } catch (e: Exception) {
                        return DataMismatchToPattern(
                            name = "bids.details.id",
                            pattern = UUID_PATTERN,
                            actualValue = id
                        ).asFailure()
                    }

                    return BidDetails(id = parsedId).asSuccess()
                }
            }
        }
    }
}
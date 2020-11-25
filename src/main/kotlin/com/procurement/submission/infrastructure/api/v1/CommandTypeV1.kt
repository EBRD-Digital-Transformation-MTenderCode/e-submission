package com.procurement.submission.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.infrastructure.api.Action

enum class CommandTypeV1(override val key: String) : Action {
    APPLY_EVALUATED_AWARDS("applyAwardingRes"),
    BID_WITHDRAWN("bidWithdrawn"),
    CHECK_PERIOD("checkPeriod"),
    CHECK_PERIOD_END_DATE("checkPeriodEndDate"),
    CHECK_TOKEN_OWNER("checkTokenOwner"),
    CREATE_BID("createBid"),
    EXTEND_TENDER_PERIOD("extendTenderPeriod"),
    FINAL_BIDS_STATUS_BY_LOTS("finalBidsStatusByLots"),
    GET_BIDS_AUCTION("getBidsAuction"),
    GET_BIDS_BY_LOTS("getBidsByLots"),
    GET_BIDS_FOR_EVALUATION("getBidsForEvaluation"),
    GET_DOCS_OF_CONSIDERED_BID("getDocsOfConsideredBid"),
    OPEN_BID_DOCS("openBidDocs"),
    OPEN_BIDS_FOR_PUBLISHING("openBidsForPublishing"),
    SAVE_NEW_PERIOD("saveNewPeriod"),
    SAVE_PERIOD("savePeriod"),
    UPDATE_BID("updateBid"),
    UPDATE_BID_BY_AWARD_STATUS("updateBidBAwardStatus"),
    UPDATE_BID_DOCS("updateBidDocs"),
    VALIDATE_PERIOD("validatePeriod"),
    ;

    @JsonValue
    fun value(): String = this.key

    override fun toString(): String {
        return this.key
    }
}

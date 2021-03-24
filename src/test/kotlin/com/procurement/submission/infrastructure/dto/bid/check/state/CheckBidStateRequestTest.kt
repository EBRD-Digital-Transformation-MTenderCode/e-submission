package com.procurement.submission.infrastructure.dto.bid.check.state

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckBidStateRequest
import org.junit.jupiter.api.Test

class CheckBidStateRequestTest : AbstractDTOTestBase<CheckBidStateRequest>(CheckBidStateRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/check/state/check_bid_state_request_full.json")
    }
}
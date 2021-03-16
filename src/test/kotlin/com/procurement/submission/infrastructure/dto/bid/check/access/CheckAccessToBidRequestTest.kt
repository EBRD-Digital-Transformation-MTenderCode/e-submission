package com.procurement.submission.infrastructure.dto.bid.check.access

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.CheckAccessToBidRequest
import org.junit.jupiter.api.Test

class CheckAccessToBidRequestTest : AbstractDTOTestBase<CheckAccessToBidRequest>(CheckAccessToBidRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/check/access/check_access_to_bid_request_full.json")
    }
}
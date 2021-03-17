package com.procurement.submission.infrastructure.dto.bid.set.state

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.SetStateForBidsRequest
import org.junit.jupiter.api.Test

class SetStateForBidsRequestTest : AbstractDTOTestBase<SetStateForBidsRequest>(SetStateForBidsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/set/state/request_set_state_for_bid_full.json")
    }
}
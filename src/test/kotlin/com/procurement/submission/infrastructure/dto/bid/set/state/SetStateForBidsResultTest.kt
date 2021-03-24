package com.procurement.submission.infrastructure.dto.bid.set.state

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.SetStateForBidsResult
import org.junit.jupiter.api.Test

class SetStateForBidsResultTest : AbstractDTOTestBase<SetStateForBidsResult>(SetStateForBidsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/set/state/response_set_state_for_bid_full.json")
    }
}
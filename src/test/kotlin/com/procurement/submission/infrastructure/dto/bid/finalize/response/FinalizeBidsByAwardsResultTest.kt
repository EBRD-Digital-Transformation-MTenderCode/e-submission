package com.procurement.submission.infrastructure.dto.bid.finalize.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.FinalizeBidsByAwardsResult
import org.junit.jupiter.api.Test

class FinalizeBidsByAwardsResultTest : AbstractDTOTestBase<FinalizeBidsByAwardsResult>(FinalizeBidsByAwardsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/finalize/response/finalize_bids_by_awards_response_full.json")
    }
}
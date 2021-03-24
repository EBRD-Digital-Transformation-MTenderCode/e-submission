package com.procurement.submission.infrastructure.dto.bid.finalize.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.FinalizeBidsByAwardsRequest
import org.junit.jupiter.api.Test

class FinalizeBidsByAwardsRequestTest : AbstractDTOTestBase<FinalizeBidsByAwardsRequest>(FinalizeBidsByAwardsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/finalize/request/finalize_bids_by_awards_request_full.json")
    }
}
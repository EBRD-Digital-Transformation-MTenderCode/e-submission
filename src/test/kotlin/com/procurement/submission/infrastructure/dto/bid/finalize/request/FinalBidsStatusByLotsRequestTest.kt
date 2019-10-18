package com.procurement.submission.infrastructure.dto.bid.finalize.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class FinalBidsStatusByLotsRequestTest :
    AbstractDTOTestBase<FinalBidsStatusByLotsRequest>(FinalBidsStatusByLotsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/finalize/request/request_final_bids_status_by_lots_full.json")
    }
}

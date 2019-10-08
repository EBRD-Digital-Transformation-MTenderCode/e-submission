package com.procurement.submission.infrastructure.dto.bid.finalize.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class FinalBidsStatusByLotsResponseTest :
    AbstractDTOTestBase<FinalBidsStatusByLotsResponse>(FinalBidsStatusByLotsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/finalize/response/response_final_bids_status_by_lots_full.json")
    }
}

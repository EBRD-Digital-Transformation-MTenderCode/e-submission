package com.procurement.submission.infrastructure.dto.bid.getbidsbylots.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.dto.bid.bidsbylots.request.GetBidsByLotsRequest
import org.junit.jupiter.api.Test

class GetBidsByLotsRequestTest :
    AbstractDTOTestBase<GetBidsByLotsRequest>(GetBidsByLotsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/request/get_bids_by_lots_request_full.json")
    }
}

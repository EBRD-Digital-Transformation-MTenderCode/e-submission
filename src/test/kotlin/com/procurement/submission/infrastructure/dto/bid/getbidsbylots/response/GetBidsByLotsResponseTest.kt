package com.procurement.submission.infrastructure.dto.bid.getbidsbylots.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v1.model.response.GetBidsByLotsResponse
import org.junit.jupiter.api.Test

class GetBidsByLotsResponseTest :
    AbstractDTOTestBase<GetBidsByLotsResponse>(GetBidsByLotsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_3.json")
    }
}

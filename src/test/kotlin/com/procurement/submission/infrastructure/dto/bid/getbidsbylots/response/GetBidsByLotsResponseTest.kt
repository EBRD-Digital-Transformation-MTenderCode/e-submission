package com.procurement.submission.infrastructure.dto.bid.getbidsbylots.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.dto.bid.bidsbylots.response.GetBidsByLotsResponse
import org.junit.jupiter.api.Test

class GetBidsByLotsResponseTest :
    AbstractDTOTestBase<GetBidsByLotsResponse>(GetBidsByLotsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_full.json")
    }

    @Test
    fun fully1() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_1.json")
    }

    @Test
    fun fully2() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_2.json")
    }

    @Test
    fun fully3() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_3.json")
    }

    @Test
    fun fully4() {
        testBindingAndMapping("json/infrastructure/dto/bid/getbidsbylots/response/get_bids_by_lots_response_4.json")
    }
}

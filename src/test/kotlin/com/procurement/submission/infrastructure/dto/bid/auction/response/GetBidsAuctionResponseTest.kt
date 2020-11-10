package com.procurement.submission.infrastructure.dto.bid.auction.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v1.model.response.GetBidsAuctionResponse
import org.junit.jupiter.api.Test

class GetBidsAuctionResponseTest : AbstractDTOTestBase<GetBidsAuctionResponse>(GetBidsAuctionResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/auction/response/response_get_bids_auction_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/auction/response/response_get_bids_auction_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/auction/response/response_get_bids_auction_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/auction/response/response_get_bids_auction_required_3.json")
    }
}
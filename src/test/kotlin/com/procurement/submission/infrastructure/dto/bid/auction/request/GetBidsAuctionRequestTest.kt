package com.procurement.submission.infrastructure.dto.bid.auction.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.api.v1.request.GetBidsAuctionRequest
import org.junit.jupiter.api.Test

class GetBidsAuctionRequestTest : AbstractDTOTestBase<GetBidsAuctionRequest>(GetBidsAuctionRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/auction/request/request_get_bid_auction_full.json")
    }
}
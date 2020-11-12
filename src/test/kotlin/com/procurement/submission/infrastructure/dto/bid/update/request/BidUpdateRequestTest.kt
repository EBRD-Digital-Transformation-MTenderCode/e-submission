package com.procurement.submission.infrastructure.dto.bid.update.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v1.model.request.BidUpdateRequest
import org.junit.jupiter.api.Test

class BidUpdateRequestTest : AbstractDTOTestBase<BidUpdateRequest>(BidUpdateRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/update/request/request_bid_update_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/update/request/request_bid_update_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/update/request/request_bid_update_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/update/request/request_bid_update_required_3.json")
    }
}
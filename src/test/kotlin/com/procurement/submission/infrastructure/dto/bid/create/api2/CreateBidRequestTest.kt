package com.procurement.submission.infrastructure.dto.bid.create.api2

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.dto.bid.create.CreateBidRequest
import org.junit.jupiter.api.Test

class CreateBidRequestTest : AbstractDTOTestBase<CreateBidRequest>(CreateBidRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_request_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_request_required.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_request_required_1.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_request_required_2.json")
    }

}
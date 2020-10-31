package com.procurement.submission.infrastructure.dto.bid.create.api2

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.dto.bid.create.CreateBidResult
import org.junit.jupiter.api.Test

class CreateBidResultTest : AbstractDTOTestBase<CreateBidResult>(CreateBidResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_result_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_result_required.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_result_required_1.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/api2/create_bid_result_required_2.json")
    }
}
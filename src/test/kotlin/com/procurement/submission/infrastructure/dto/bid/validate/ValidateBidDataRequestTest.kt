package com.procurement.submission.infrastructure.dto.bid.validate

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.ValidateBidDataRequest
import org.junit.jupiter.api.Test

class ValidateBidDataRequestTest : AbstractDTOTestBase<ValidateBidDataRequest>(ValidateBidDataRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/validate/request_validate_bid_data_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/validate/request_validate_bid_data_required.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/validate/request_validate_bid_data_required_1.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/validate/request_validate_bid_data_required_2.json")
    }

}
package com.procurement.submission.infrastructure.dto.bid.create.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.model.dto.request.BidCreateRequest
import org.junit.jupiter.api.Test

class BidCreateRequestTest : AbstractDTOTestBase<BidCreateRequest>(BidCreateRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/request/request_bid_create_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/request/request_bid_create_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/request/request_bid_create_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/request/request_bid_create_required_3.json")
    }

}
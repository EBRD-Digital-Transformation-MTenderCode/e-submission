package com.procurement.submission.infrastructure.dto.bid.create.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.model.dto.response.BidCreateResponse
import org.junit.jupiter.api.Test

class BidCreateResponseTest : AbstractDTOTestBase<BidCreateResponse>(BidCreateResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/create/response/response_bid_create_full.json")
    }
}
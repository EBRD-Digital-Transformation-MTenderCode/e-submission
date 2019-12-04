package com.procurement.submission.infrastructure.dto.bid.opendoc.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class OpenBidDocsResponseTest :
    AbstractDTOTestBase<OpenBidDocsResponse>(OpenBidDocsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/opendoc/response/open_bid_docs_response_full.json")
    }

    @Test
    fun fully1() {
        testBindingAndMapping("json/infrastructure/dto/bid/opendoc/response/open_bid_docs_response_1.json")
    }

    @Test
    fun fully2() {
        testBindingAndMapping("json/infrastructure/dto/bid/opendoc/response/open_bid_docs_response_2.json")
    }
}
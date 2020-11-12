package com.procurement.submission.infrastructure.dto.bid.opendoc.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v1.model.request.OpenBidDocsRequest
import org.junit.jupiter.api.Test

class OpenBidDocsRequestTest :
    AbstractDTOTestBase<OpenBidDocsRequest>(OpenBidDocsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/opendoc/request/open_bid_docs_request_full.json")
    }
}
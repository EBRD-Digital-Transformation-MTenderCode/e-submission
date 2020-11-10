package com.procurement.submission.infrastructure.dto.bid.publishing.request

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.api.v1.request.OpenBidsForPublishingRequest
import org.junit.jupiter.api.Test

class OpenBidsForPublishingRequestTest :
    AbstractDTOTestBase<OpenBidsForPublishingRequest>(OpenBidsForPublishingRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/publishing/request/request_open_bids_for_publishing_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/bid/publishing/request/request_open_bids_for_publishing_required_1.json")
    }
}

package com.procurement.submission.infrastructure.dto.bid.publishing.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.model.dto.response.OpenBidsForPublishingResponse
import org.junit.jupiter.api.Test

class OpenBidsForPublishingResponseTest : AbstractDTOTestBase<OpenBidsForPublishingResponse>(OpenBidsForPublishingResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/publishing/response/response_open_bids_for_publishing_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/publishing/response/response_open_bids_for_publishing_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/publishing/response/response_open_bids_for_publishing_required_2.json")
    }

}
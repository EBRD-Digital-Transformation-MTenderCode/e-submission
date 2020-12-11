package com.procurement.submission.infrastructure.dto.bid.get

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.GetBidsForPacsRequest
import org.junit.jupiter.api.Test

class GetBidsForPacsRequestTest : AbstractDTOTestBase<GetBidsForPacsRequest>(GetBidsForPacsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_bids_for_pacs_request_full.json")
    }
}

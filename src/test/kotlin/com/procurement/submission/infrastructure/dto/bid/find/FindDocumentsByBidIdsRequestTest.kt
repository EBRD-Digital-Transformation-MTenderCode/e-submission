package com.procurement.submission.infrastructure.dto.bid.find

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.FindDocumentsByBidIdsRequest
import org.junit.jupiter.api.Test

class FindDocumentsByBidIdsRequestTest : AbstractDTOTestBase<FindDocumentsByBidIdsRequest>(FindDocumentsByBidIdsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/find/find_documents_by_bid_ids_request_full.json")
    }
}

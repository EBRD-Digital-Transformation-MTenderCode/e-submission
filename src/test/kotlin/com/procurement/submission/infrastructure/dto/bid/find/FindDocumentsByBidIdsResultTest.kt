package com.procurement.submission.infrastructure.dto.bid.find

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.FindDocumentsByBidIdsResult
import org.junit.jupiter.api.Test

class FindDocumentsByBidIdsResultTest : AbstractDTOTestBase<FindDocumentsByBidIdsResult>(FindDocumentsByBidIdsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/find/find_documents_by_bid_ids_result_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/find/find_documents_by_bid_ids_result_required_1.json")
    }
}
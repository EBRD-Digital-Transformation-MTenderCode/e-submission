package com.procurement.submission.infrastructure.dto.bid.get

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.GetBidsForPacsResult
import org.junit.jupiter.api.Test

class GetBidsForPacsResultTest : AbstractDTOTestBase<GetBidsForPacsResult>(GetBidsForPacsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_bids_for_pacs_result_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_bids_for_pacs_result_required_1.json")
    }
}
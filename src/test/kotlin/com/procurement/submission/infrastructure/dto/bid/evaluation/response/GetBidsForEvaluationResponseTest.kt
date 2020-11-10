package com.procurement.submission.infrastructure.dto.bid.evaluation.response

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.api.v1.response.GetBidsForEvaluationResponse
import org.junit.jupiter.api.Test

class GetBidsForEvaluationResponseTest : AbstractDTOTestBase<GetBidsForEvaluationResponse>(GetBidsForEvaluationResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/evaluation/response/response_get_bids_for_evaluation_full.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/evaluation/response/response_get_bids_for_evaluation_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/evaluation/response/response_get_bids_for_evaluation_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/evaluation/response/response_get_bids_for_evaluation_required_3.json")
    }
}
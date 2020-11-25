package com.procurement.submission.infrastructure.dto.tender.period

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v1.model.response.ExtendTenderPeriodResponse
import org.junit.jupiter.api.Test

class ExtendTenderPeriodResponseTest :
    AbstractDTOTestBase<ExtendTenderPeriodResponse>(ExtendTenderPeriodResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/tender/period/response_extend_tender_period_full.json")
    }
}

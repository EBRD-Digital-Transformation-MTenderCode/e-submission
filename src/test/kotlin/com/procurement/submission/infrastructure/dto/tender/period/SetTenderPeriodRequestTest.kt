package com.procurement.submission.infrastructure.dto.tender.period

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.SetTenderPeriodRequest
import org.junit.jupiter.api.Test

class SetTenderPeriodRequestTest :
    AbstractDTOTestBase<SetTenderPeriodRequest>(SetTenderPeriodRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/tender/period/request_set_tender_period_full.json")
    }
}

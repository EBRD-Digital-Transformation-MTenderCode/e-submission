package com.procurement.submission.infrastructure.dto.tender.period

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.api.v1.request.CheckPeriodRequest
import org.junit.jupiter.api.Test

class CheckPeriodRequestTest :
    AbstractDTOTestBase<CheckPeriodRequest>(CheckPeriodRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/tender/period/request_check_period_full.json")
    }
}

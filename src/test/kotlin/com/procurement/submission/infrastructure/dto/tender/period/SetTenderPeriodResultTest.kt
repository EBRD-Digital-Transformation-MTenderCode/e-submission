package com.procurement.submission.infrastructure.dto.tender.period

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodResult
import org.junit.jupiter.api.Test

class SetTenderPeriodResultTest :
    AbstractDTOTestBase<SetTenderPeriodResult>(SetTenderPeriodResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/tender/period/result_set_tender_period_full.json")
    }
}

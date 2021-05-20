package com.procurement.submission.infrastructure.dto.bid.processing

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.PersonesProcessingResult
import org.junit.jupiter.api.Test

class PersonesProcessingResultTest : AbstractDTOTestBase<PersonesProcessingResult>(
    PersonesProcessingResult::class.java
) {
    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/processing/persones_processing_result_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/bid/processing/persones_processing_result_required1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/bid/processing/persones_processing_result_required2.json")
    }
}
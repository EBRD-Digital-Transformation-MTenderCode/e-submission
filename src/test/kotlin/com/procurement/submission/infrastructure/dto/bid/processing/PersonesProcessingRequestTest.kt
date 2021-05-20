package com.procurement.submission.infrastructure.dto.bid.processing

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.PersonesProcessingRequest
import org.junit.jupiter.api.Test

class PersonesProcessingRequestTest : AbstractDTOTestBase<PersonesProcessingRequest>(
    PersonesProcessingRequest::class.java
) {
    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/processing/persones_processing_request_fully.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/bid/processing/persones_processing_request_required1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/bid/processing/persones_processing_request_required2.json")
    }
}
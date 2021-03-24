package com.procurement.submission.infrastructure.dto.bid.get

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.response.GetOrganizationsByReferencesFromPacsResult
import org.junit.jupiter.api.Test

class GetOrganizationsByReferencesFromPacsResultTest : AbstractDTOTestBase<GetOrganizationsByReferencesFromPacsResult>(GetOrganizationsByReferencesFromPacsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_organizations_by_references_from_pacs_result_fully.json")
    }

    @Test
    fun required_1() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_organizations_by_references_from_pacs_result_required_1.json")
    }

    @Test
    fun required_2() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_organizations_by_references_from_pacs_result_required_2.json")
    }

    @Test
    fun required_3() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_organizations_by_references_from_pacs_result_required_3.json")
    }
}
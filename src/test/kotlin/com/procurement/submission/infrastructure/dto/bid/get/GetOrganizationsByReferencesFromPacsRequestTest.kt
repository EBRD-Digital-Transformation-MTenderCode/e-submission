package com.procurement.submission.infrastructure.dto.bid.get

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.GetOrganizationsByReferencesFromPacsRequest
import org.junit.jupiter.api.Test

class GetOrganizationsByReferencesFromPacsRequestTest : AbstractDTOTestBase<GetOrganizationsByReferencesFromPacsRequest>(GetOrganizationsByReferencesFromPacsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_organizations_by_references_from_pacs_request_full.json")
    }
}

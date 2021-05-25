package com.procurement.submission.infrastructure.dto.bid.get

import com.procurement.submission.infrastructure.AbstractDTOTestBase
import com.procurement.submission.infrastructure.handler.v2.model.request.GetSuppliersOwnersRequest
import org.junit.jupiter.api.Test

class GetSuppliersOwnersRequestTest : AbstractDTOTestBase<GetSuppliersOwnersRequest>(
    GetSuppliersOwnersRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/bid/get/get_suppliers_owners_request_full.json")
    }
}
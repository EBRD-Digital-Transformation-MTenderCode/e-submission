package com.procurement.submission.infrastructure.dto.bid.opendoc.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class OpenBidDocsRequest(
    @field:JsonProperty("bidId") @param:JsonProperty("bidId") val bidId: UUID
)

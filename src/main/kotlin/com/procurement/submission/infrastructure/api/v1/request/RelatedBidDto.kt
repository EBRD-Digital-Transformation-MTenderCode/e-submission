package com.procurement.submission.infrastructure.api.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedBidRq @JsonCreator constructor(

        val relatedBids: List<String>
)

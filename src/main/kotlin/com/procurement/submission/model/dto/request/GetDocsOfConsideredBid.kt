package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Document

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetDocsOfConsideredBidRq @JsonCreator constructor(

        val consideredBidId: String?
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class GetDocsOfConsideredBidRs @JsonCreator constructor(

        val consideredBid: ConsideredBid?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConsideredBid @JsonCreator constructor(

        var id: String,

        var documents: List<Document>?

)


package com.procurement.submission.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Document

data class BidUpdateDocsRq @JsonCreator constructor(

        val bid: BidUpdateDocs
)

data class BidUpdateDocs @JsonCreator constructor(

        var documents: List<Document>
)
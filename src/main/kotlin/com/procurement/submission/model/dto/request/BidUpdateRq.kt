package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Value

data class BidUpdateRq @JsonCreator constructor(

        val bid: BidUpdate
)

data class BidUpdate @JsonCreator constructor(

        var value: Value?,

        var documents: List<Document>?
)
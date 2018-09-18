package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Value
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class BidUpdateRq @JsonCreator constructor(

        @field:Valid
        val bid: BidUpdate
)

data class BidUpdate @JsonCreator constructor(

        @field:Valid
        var value: Value?,

        @field:Valid
        var documents: List<Document>?
)
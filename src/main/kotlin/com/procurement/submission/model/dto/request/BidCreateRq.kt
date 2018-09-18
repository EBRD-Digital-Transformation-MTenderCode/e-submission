package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.OrganizationReference
import com.procurement.submission.model.dto.ocds.Value
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class BidCreateRq @JsonCreator constructor(

        @field:Valid
        val bid: BidCreate
)

data class BidCreate @JsonCreator constructor(

        @field:Valid @field:NotEmpty
        val tenderers: List<OrganizationReference>,

        @field:Valid
        var value: Value?,

        @field:Valid
        var documents: List<Document>?,

        @field:NotEmpty
        val relatedLots: List<String>
)
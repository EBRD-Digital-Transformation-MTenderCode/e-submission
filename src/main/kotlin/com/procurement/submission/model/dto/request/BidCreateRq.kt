package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.OrganizationReference
import com.procurement.submission.model.dto.ocds.Value

data class BidCreateRq @JsonCreator constructor(

        val bid: BidCreate
)

data class BidCreate @JsonCreator constructor(

        val tenderers: List<OrganizationReference>,

        var value: Value,

        var documents: List<Document>?,

        val relatedLots: List<String>
)
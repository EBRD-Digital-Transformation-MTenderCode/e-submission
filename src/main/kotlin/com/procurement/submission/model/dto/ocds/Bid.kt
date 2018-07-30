package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid @JsonCreator constructor(

        var id: String,

        var date: LocalDateTime,

        var status: Status,

        var statusDetails: StatusDetails,

        @field:Valid
        val tenderers: List<OrganizationReference>,

        @field:Valid
        var value: Value?,

        @field:Valid
        var documents: List<Document>?,

        val relatedLots: List<String>
)
package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid @JsonCreator constructor(

        var id: String,

        var date: LocalDateTime,

        var status: Status,

        var statusDetails: StatusDetails,

        val tenderers: List<OrganizationReference>,

        var value: Value?,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        var documents: List<Document>?,

        val relatedLots: List<String>,

        @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
        val requirementResponses: List<RequirementResponse>
)
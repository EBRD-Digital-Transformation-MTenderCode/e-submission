package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.OrganizationReference
import com.procurement.submission.model.dto.ocds.Value
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidDto @JsonCreator constructor(

        var id: String,

        val date: LocalDateTime?,

        var createdDate: LocalDateTime,

        var pendingDate: LocalDateTime?,

        var value: Value,

        val tenderers: List<OrganizationReference>,

        val relatedLots: List<String>
)
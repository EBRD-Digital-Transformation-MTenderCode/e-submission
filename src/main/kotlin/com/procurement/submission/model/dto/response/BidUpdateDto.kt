package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails
import com.procurement.submission.model.dto.ocds.Value
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidUpdateDto(

        val id: String?,

        val date: LocalDateTime?,

        val status: Status?,

        val statusDetails: StatusDetails?,

        val tenderers: List<OrganizationReferenceDto>?,

        val value: Value?,

        val documents: List<Document>?,

        val relatedLots: List<String>?
)

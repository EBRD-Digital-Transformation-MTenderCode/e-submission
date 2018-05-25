package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.procurement.submission.model.dto.ocds.Document
import com.procurement.submission.model.dto.ocds.Status
import com.procurement.submission.model.dto.ocds.StatusDetails
import com.procurement.submission.model.dto.ocds.Value
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BidUpdateDto(

        @JsonProperty("id")
        val id: String?,

        @JsonProperty("date")
        @JsonSerialize(using = LocalDateTimeSerializer::class)
        val date: LocalDateTime?,

        @JsonProperty("status")
        val status: Status?,

        @JsonProperty("statusDetails")
        val statusDetails: StatusDetails?,

        @JsonProperty("tenderers")
        val tenderers: List<OrganizationReferenceDto>?,

        @JsonProperty("value")
        val value: Value?,

        @JsonProperty("documents")
        val documents: List<Document>?,

        @JsonProperty("relatedLots")
        val relatedLots: List<String>?
)

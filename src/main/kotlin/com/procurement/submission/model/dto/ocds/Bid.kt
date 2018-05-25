package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.Valid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid(

        @JsonProperty("id")
        var id: String?,

        @JsonProperty("date")
        @JsonSerialize(using = JsonDateSerializer::class)
        @JsonDeserialize(using = JsonDateDeserializer::class)
        var date: LocalDateTime?,

        @JsonProperty("status")
        var status: Status?,

        @JsonProperty("statusDetails")
        var statusDetails: StatusDetails?,

        @JsonProperty("tenderers") @Valid
        val tenderers: List<OrganizationReference>?,

        @JsonProperty("value") @Valid
        var value: Value?,

        @JsonProperty("documents") @Valid
        var documents: List<Document>?,

        @JsonProperty("relatedLots")
        val relatedLots: List<String>?
)
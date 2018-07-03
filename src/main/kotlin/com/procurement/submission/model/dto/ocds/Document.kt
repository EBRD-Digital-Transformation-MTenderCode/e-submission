package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val documentType: DocumentType,

        val title: String?,

        val description: String?,

        @field:NotNull
        val language: String,

        val relatedLots: HashSet<String>?
)
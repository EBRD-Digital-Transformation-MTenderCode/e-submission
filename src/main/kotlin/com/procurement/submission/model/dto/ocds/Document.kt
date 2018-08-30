package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val documentType: DocumentType,

        var title: String?,

        var description: String?,

        @field:NotEmpty
        var relatedLots: HashSet<String>?
)
package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.domain.model.enums.DocumentType
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(

    val id: String,

    val documentType: DocumentType,

    var title: String?,

    var description: String?,

    var relatedLots: List<String>?
)
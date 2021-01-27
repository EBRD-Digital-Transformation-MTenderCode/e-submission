package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.domain.model.enums.DocumentType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(

    val id: String,

    val documentType: DocumentType,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var description: String?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var relatedLots: List<String>?
)
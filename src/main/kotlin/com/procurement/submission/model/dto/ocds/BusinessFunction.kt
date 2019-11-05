package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.submission.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.submission.domain.model.enums.BusinessFunctionType
import java.time.LocalDateTime

data class BusinessFunction(
    val id: String,
    val type: BusinessFunctionType,
    val jobTitle: String,
    val period: Period,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val documents: List<Document>?
) {
    data class Period(
        val startDate: LocalDateTime
    )

    data class Document (
        val id: String,
        val documentType: BusinessFunctionDocumentType,
        var title: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        var description: String?
    )
}
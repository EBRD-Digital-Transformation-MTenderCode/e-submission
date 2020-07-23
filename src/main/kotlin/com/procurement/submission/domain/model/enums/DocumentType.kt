package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DocumentType(@JsonValue override val key: String) : EnumElementProvider.Key {

    SUBMISSION_DOCUMENTS("submissionDocuments"),
    ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    ILLUSTRATION("illustration"),
    COMMERCIAL_OFFER("x_commercialOffer"),
    QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    TECHNICAL_DOCUMENTS("x_technicalDocuments");

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentType.orThrow(name)
    }
}
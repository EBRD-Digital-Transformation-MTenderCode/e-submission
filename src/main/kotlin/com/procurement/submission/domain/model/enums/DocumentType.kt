package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class DocumentType(@JsonValue override val key: String) : EnumElementProvider.Key {

    COMMERCIAL_OFFER("x_commercialOffer"),
    ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    ILLUSTRATION("illustration"),
    QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    REGULATORY_DOCUMENT("regulatoryDocument"),
    SUBMISSION_DOCUMENTS("submissionDocuments"),
    TECHNICAL_DOCUMENTS("x_technicalDocuments");

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentType.orThrow(name)
    }
}
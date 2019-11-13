package com.procurement.submission.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException
import java.util.*

enum class DocumentType constructor(private val value: String) {

    SUBMISSION_DOCUMENTS("submissionDocuments"),
    ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    ILLUSTRATION("illustration"),
    COMMERCIAL_OFFER("x_commercialOffer"),
    QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    TECHNICAL_DOCUMENTS("x_technicalDocuments");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, DocumentType>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): DocumentType {
            return CONSTANTS[value]
                ?: throw EnumException(
                    DocumentType::class.java.name,
                    value,
                    Arrays.toString(values())
                )
        }
    }
}
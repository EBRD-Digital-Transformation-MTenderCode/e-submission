package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException
import java.util.*

enum class AwardCriteria(@JsonValue val value: String) {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria"),
    LOWEST_COST("lowestCost"),
    BEST_PROPOSAL("bestProposal"),
    BEST_VALUE_TO_GOVERNMENT("bestValueToGovernment"),
    SINGLE_BID_ONLY("singleBidOnly");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val CONSTANTS = HashMap<String, AwardCriteria>()

        init {
            values().forEach { CONSTANTS[it.value] = it }
        }

        fun fromValue(v: String): AwardCriteria {
            return CONSTANTS[v] ?: throw EnumException(AwardCriteria::class.java.name, v, values().toString())
        }
    }
}

enum class DocumentType constructor(private val value: String) {

    SUBMISSION_DOCUMENTS("submissionDocuments"),
    ILLUSTRATION("illustration"),
    COMMERCIAL_OFFER("x_commercialOffer"),
    QUALIFICATION_DOCUMENTS("x_qualificationDocuments"),
    ELIGIBILITY_DOCUMENTS("x_eligibilityDocuments"),
    TECHNICAL_PROPOSAL("technicalProposal"),
    SELECTION_DOCUMENTS("selectionDocuments"),
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
                    ?: throw EnumException(DocumentType::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class Status constructor(private val value: String) {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, Status>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): Status {
            return CONSTANTS[value] ?: throw EnumException(Status::class.java.name, value, Arrays.toString(values()))
        }
    }
}

enum class StatusDetails constructor(private val value: String) {
    INVITED("invited"),
    PENDING("pending"),
    VALID("valid"),
    DISQUALIFIED("disqualified"),
    WITHDRAWN("withdrawn"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, StatusDetails>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): StatusDetails {
            return CONSTANTS[value]
                    ?: throw EnumException(StatusDetails::class.java.name, value, Arrays.toString(values()))
        }
    }
}


enum class AwardStatusDetails constructor(private val value: String) {
    PENDING("pending"),
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
    CONSIDERATION("consideration"),
    EMPTY("empty");

    override fun toString(): String {
        return this.value
    }

    @JsonValue
    fun value(): String {
        return this.value
    }

    companion object {

        private val CONSTANTS = HashMap<String, AwardStatusDetails>()

        init {
            for (c in values()) {
                CONSTANTS[c.value] = c
            }
        }

        @JsonCreator
        fun fromValue(value: String): AwardStatusDetails {
            return CONSTANTS[value]
                    ?: throw EnumException(AwardStatusDetails::class.java.name, value, Arrays.toString(values()))
        }
    }

}
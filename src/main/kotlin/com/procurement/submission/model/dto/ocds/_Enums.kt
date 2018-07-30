package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException
import java.util.*

enum class DocumentType constructor(private val value: String) {

    SUBMISSION_DOCUMENTS("submissionDocuments"),
    ILLUSTRATION("illustration");

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
    ACTIVE("active"),
    UNSUCCESSFUL("unsuccessful"),
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
package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.submission.exception.EnumException
import java.util.*

enum class AwardCriteria(@JsonValue val value: String) {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria");

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

enum class Status constructor(val value: String) {
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
        private val CONSTANTS: Map<String, Status> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): Status = CONSTANTS[value.toUpperCase()]
            ?: throw EnumException(
                enumType = Status::class.java.name,
                value = value,
                values = values().toString()
            )
    }
}

enum class StatusDetails constructor(val value: String) {
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
        private val CONSTANTS: Map<String, StatusDetails> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): StatusDetails = CONSTANTS[value.toUpperCase()]
            ?: throw EnumException(
                enumType = StatusDetails::class.java.name,
                value = value,
                values = values().toString()
            )
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
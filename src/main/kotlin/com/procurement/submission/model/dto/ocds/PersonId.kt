package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.io.Serializable

class PersonId private constructor(private val value: String) : Serializable {

    @JsonValue
    override fun toString(): String = value

    override fun equals(other: Any?): Boolean = if (this !== other)
        other is PersonId
            && this.value == other.value
    else
        true

    override fun hashCode(): Int = value.hashCode()

    companion object {

        @JvmStatic
        @JsonCreator
        fun parse(text: String): PersonId? = if (text.isBlank())
            null
        else
            PersonId(text)

        fun generate(scheme: String, id: String): PersonId = PersonId("$scheme-$id")
    }
}

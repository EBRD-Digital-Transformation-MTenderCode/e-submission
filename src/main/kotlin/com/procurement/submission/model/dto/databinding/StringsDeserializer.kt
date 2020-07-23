package com.procurement.submission.model.dto.databinding

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import java.io.IOException


class StringsDeserializer : JsonDeserializer<String>() {

    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): String {
        if (jsonParser.currentToken != JsonToken.VALUE_STRING) {
            throw ErrorException(
                ErrorType.INVALID_JSON_TYPE,
                jsonParser.currentName
            )
        }
        return jsonParser.valueAsString
    }
}
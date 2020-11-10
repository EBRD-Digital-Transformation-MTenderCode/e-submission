package com.procurement.submission.infrastructure.bind.api.version

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.submission.infrastructure.api.ApiVersion
import java.io.IOException

class ApiVersionDeserializer : JsonDeserializer<ApiVersion>() {
    companion object {
        fun deserialize(text: String) = ApiVersion.orThrow(text) {
            IllegalAccessException("Invalid format of the api version. Expected: '${ApiVersion.pattern}', actual: '$text'.")
        }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ApiVersion =
        deserialize(jsonParser.text)
}

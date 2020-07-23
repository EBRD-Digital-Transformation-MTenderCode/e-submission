package com.procurement.submission.infrastructure.web.api.version.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.submission.infrastructure.web.api.version.ApiVersion2
import java.io.IOException

class ApiVersion2Serializer : JsonSerializer<ApiVersion2>() {
    companion object {
        fun serialize(apiVersion: ApiVersion2): String = "${apiVersion.major}.${apiVersion.minor}.${apiVersion.patch}"
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(apiVersion: ApiVersion2, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        jsonGenerator.writeString(
            serialize(
                apiVersion
            )
        )
    }
}

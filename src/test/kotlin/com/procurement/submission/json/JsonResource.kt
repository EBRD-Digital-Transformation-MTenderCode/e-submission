package com.procurement.submission.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.submission.json.exception.JsonBindingException
import com.procurement.submission.json.exception.JsonFileNotFoundException
import com.procurement.submission.json.exception.JsonMappingException
import com.procurement.submission.json.exception.JsonParsingException
import com.procurement.submission.model.dto.databinding.IntDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import com.procurement.submission.model.dto.databinding.StringsDeserializer
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

typealias JSON = String

fun loadJson(fileName: String): JSON {
    return ClassPathResource.getFilePath(fileName)?.let { pathToFile ->
        val path = Paths.get(pathToFile)
        val buffer = Files.readAllBytes(path)
        String(buffer, Charset.defaultCharset())
    } ?: throw JsonFileNotFoundException("Error loading JSON. File by path: $fileName not found.")
}

fun JSON.compact(): JSON {
    val factory = JsonFactory()
    val parser = factory.createParser(this)
    val out = StringWriter()
    factory.createGenerator(out).use { gen ->
        while (parser.nextToken() != null) {
            gen.copyCurrentEvent(parser)
        }
    }
    return out.buffer.toString()
}

inline fun <reified T : Any> JsonNode.toObject(): T = try {
    JsonMapper.mapper.treeToValue(this, T::class.java)
} catch (exception: IOException) {
    val className = this::class.java.canonicalName
    throw JsonBindingException("Error binding JSON to an object of type '$className'.", exception)
}

inline fun <reified T : Any> JSON.toObject(): T = this.toObject(T::class.java)

fun <T : Any> JSON.toObject(target: Class<T>): T = try {
    JsonMapper.mapper.readValue(this, target)
} catch (exception: Exception) {
    throw JsonBindingException("Error binding JSON to an object of type '${target.canonicalName}'.", exception)
}

fun <T : Any> T.toJson(): JSON = try {
    JsonMapper.mapper.writeValueAsString(this)
} catch (exception: JsonProcessingException) {
    val className = this::class.java.canonicalName
    throw JsonMappingException("Error mapping an object of type '$className' to JSON.", exception)
}

fun JSON.toNode(): JsonNode = try {
    JsonMapper.mapper.readTree(this)
} catch (exception: JsonProcessingException) {
    throw JsonParsingException("Error parsing JSON to JsonNode.", exception)
}

private object ClassPathResource {
    fun getFilePath(fileName: String): String? = javaClass.classLoader.getResource(fileName)?.path
}

object JsonMapper {
    val mapper = ObjectMapper().apply {
        val module = SimpleModule()
        module.addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        module.addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())
        module.addDeserializer(String::class.java, StringsDeserializer())
        module.addDeserializer(Int::class.java, IntDeserializer())
        registerModule(module)
        registerKotlinModule()
        registerModule(extendModule())

        configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
        configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private fun extendModule() =
        SimpleModule().apply {
            addDeserializer(String::class.java, StringsDeserializer())
            addDeserializer(Int::class.java, IntDeserializer())
        }
}

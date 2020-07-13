package com.procurement.submission.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.submission.infrastructure.web.api.version.jackson.ApiVersion2Module
import com.procurement.submission.model.dto.databinding.IntDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import com.procurement.submission.model.dto.databinding.StringsDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime


@Configuration
class ObjectMapperConfig(@Autowired objectMapper: ObjectMapper) {

    init {
        val module = SimpleModule()
        module.addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        module.addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())
        module.addDeserializer(String::class.java, StringsDeserializer())
        module.addDeserializer(Int::class.java, IntDeserializer())
        objectMapper.registerModule(module)
        objectMapper.registerModule(ApiVersion2Module())
        objectMapper.registerKotlinModule()
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
    }
}

package com.procurement.submission.infrastructure.handler.bid.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.submission.infrastructure.bind.amount.AmountModule
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueModule
import com.procurement.submission.infrastructure.handler.bid.command.CommandIdDeserializer
import com.procurement.submission.infrastructure.handler.bid.command.CommandIdSerializer
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.web.api.version.jackson.ApiVersion2Module
import com.procurement.submission.model.dto.databinding.IntDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import com.procurement.submission.model.dto.databinding.StringsDeserializer
import java.time.LocalDateTime

fun ObjectMapper.configuration() {
    val module = SimpleModule().apply {
        addSerializer(CommandId::class.java, CommandIdSerializer())
        addDeserializer(CommandId::class.java, CommandIdDeserializer())

        addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())

        addDeserializer(String::class.java, StringsDeserializer())
        addDeserializer(Int::class.java, IntDeserializer())
    }

    this.registerModule(module)
    this.registerModule(ApiVersion2Module())
    this.registerModule(RequirementValueModule())
    this.registerModule(AmountModule())
    this.registerKotlinModule()

    this.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    this.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
    this.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false)

    this.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}

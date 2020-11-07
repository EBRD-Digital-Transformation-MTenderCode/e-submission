package com.procurement.submission.infrastructure.handler.bid.command

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.submission.infrastructure.model.CommandId

class CommandIdSerializer : JsonSerializer<CommandId>() {
    companion object {
        fun serialize(commandId: CommandId): String = commandId.underlying
    }

    override fun serialize(commandId: CommandId, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(serialize(commandId))
}

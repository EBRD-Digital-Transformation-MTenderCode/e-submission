package com.procurement.submission.infrastructure.bind.date

import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.LocalDateTime

class JsonDateModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())
    }
}

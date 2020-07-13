package com.procurement.submission.infrastructure.web.api.version.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.submission.infrastructure.web.api.version.ApiVersion2

class ApiVersion2Module : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(
            ApiVersion2::class.java,
            ApiVersion2Serializer()
        )
        addDeserializer(
            ApiVersion2::class.java,
            ApiVersion2Deserializer()
        )
    }
}

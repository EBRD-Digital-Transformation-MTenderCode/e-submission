package com.procurement.submission.infrastructure.bind.criteria

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.submission.application.model.data.RequirementRsValue

class RequirementValueModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(
            RequirementRsValue::class.java,
            RequirementValueSerializer()
        )
        addDeserializer(
            RequirementRsValue::class.java,
            RequirementValueDeserializer()
        )
    }
}

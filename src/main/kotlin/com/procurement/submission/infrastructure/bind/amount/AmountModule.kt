package com.procurement.submission.infrastructure.bind.amount

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.submission.model.dto.ocds.Amount

class AmountModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Amount::class.java, AmountSerializer())
        addDeserializer(Amount::class.java, AmountDeserializer())
    }
}

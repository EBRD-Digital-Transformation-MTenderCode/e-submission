package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.submission.infrastructure.bind.MoneyDeserializer
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Value @JsonCreator constructor(

        @field:JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        val currency: String
)
package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.submission.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Value(

        @JsonProperty("amount") @NotNull
        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        @JsonProperty("currency") @NotNull
        val currency: Currency
)
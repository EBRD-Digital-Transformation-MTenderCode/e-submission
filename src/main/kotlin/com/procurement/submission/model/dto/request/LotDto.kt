package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class LotDto(

        @JsonProperty("id") @NotNull
        val id: String
)

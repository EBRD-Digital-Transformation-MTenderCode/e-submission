package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class LotsDto(

        @JsonProperty("lots") @NotEmpty
        val lots: List<LotDto>?
)

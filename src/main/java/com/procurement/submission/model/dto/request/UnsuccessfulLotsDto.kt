package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class UnsuccessfulLotsDto(

        @JsonProperty("unsuccessfulLots") @NotEmpty
        val lots: List<LotDto>?
)

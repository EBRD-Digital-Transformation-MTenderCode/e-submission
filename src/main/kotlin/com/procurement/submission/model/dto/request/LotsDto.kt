package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotEmpty

data class LotsDto @JsonCreator constructor(

        @field:NotEmpty
        val lots: List<LotDto>?
)

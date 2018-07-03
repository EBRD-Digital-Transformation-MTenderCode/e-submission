package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class LotDto @JsonCreator constructor(

        @field:NotNull
        val id: String
)

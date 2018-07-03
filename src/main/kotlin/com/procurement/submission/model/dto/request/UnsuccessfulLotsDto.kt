package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotEmpty

data class UnsuccessfulLotsDto @JsonCreator constructor(

        @field:NotEmpty
        val unsuccessfulLots: List<LotDto>?
)

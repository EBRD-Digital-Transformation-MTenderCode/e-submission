package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotEmpty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UnsuccessfulLotsDto @JsonCreator constructor(

        @field:NotEmpty
        val unsuccessfulLots: List<LotDto>?
)

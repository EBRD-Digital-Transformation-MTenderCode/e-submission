package com.procurement.submission.model.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class CheckPeriodResponseDto @JsonCreator constructor(

        @get:JsonProperty("isPeriodChanged")
        val isPeriodChanged: Boolean?
)

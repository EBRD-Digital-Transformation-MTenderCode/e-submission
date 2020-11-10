package com.procurement.submission.infrastructure.api.v1.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckPeriodRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: String
)
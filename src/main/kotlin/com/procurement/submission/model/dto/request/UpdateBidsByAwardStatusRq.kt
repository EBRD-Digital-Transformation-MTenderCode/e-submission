package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateBidsByAwardStatusRq @JsonCreator constructor(

        val bidId: String,

        val awardStatusDetails: String
)

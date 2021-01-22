package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty

data class RelatedDocument(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String
)
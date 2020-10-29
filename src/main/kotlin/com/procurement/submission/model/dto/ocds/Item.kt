package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.item.ItemId

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item @JsonCreator constructor(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: ItemId,
    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit
) {
    data class Unit(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
        @param:JsonProperty("value") @field:JsonProperty("value") val value: Value
    )
}
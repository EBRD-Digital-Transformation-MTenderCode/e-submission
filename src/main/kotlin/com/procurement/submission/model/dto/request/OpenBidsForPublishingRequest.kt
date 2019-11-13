package com.procurement.submission.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.enums.AwardCriteriaDetails
import com.procurement.submission.domain.model.enums.AwardStatusDetails
import com.procurement.submission.infrastructure.bind.money.MoneyDeserializer
import com.procurement.submission.infrastructure.bind.money.MoneySerializer
import com.procurement.submission.model.dto.databinding.JsonDateDeserializer
import com.procurement.submission.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import java.util.*

data class OpenBidsForPublishingRequest(
    @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails,
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: UUID,

        @JsonDeserialize(using = MoneyDeserializer::class)
        @JsonSerialize(using = MoneySerializer::class)
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Money,

        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>
    ) {
        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
        )
    }
}
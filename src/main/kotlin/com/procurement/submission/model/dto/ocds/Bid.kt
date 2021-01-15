package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.submission.domain.model.Money
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails
import com.procurement.submission.infrastructure.bind.money.MoneyDeserializer
import com.procurement.submission.infrastructure.bind.money.MoneySerializer
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid @JsonCreator constructor(

    var id: String,

    var date: LocalDateTime,

    var status: Status,

    var statusDetails: StatusDetails,

    val tenderers: List<Organization>,

    @JsonDeserialize(using = MoneyDeserializer::class)
        @JsonSerialize(using = MoneySerializer::class)
        var value: Money?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
        var documents: List<Document>?,

    val relatedLots: List<String>,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val requirementResponses: List<RequirementResponse>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
        var items: List<Item>? = null
)
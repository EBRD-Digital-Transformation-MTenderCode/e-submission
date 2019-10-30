package com.procurement.submission.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueDeserializer
import com.procurement.submission.infrastructure.bind.criteria.RequirementValueSerializer

data class RequirementResponse @JsonCreator constructor(
    val id: String,
    val title: String,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val description: String?,

    @JsonDeserialize(using = RequirementValueDeserializer::class)
    @JsonSerialize(using = RequirementValueSerializer::class)
    val value: RequirementRsValue,

    val requirement: Requirement,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val period: Period?
)
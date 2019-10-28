package com.procurement.submission.model.dto.ocds

import com.procurement.submission.application.model.data.RequirementRsValue

data class RequirementResponse(
    val id: String,
    val title: String,
    val description: String,
    val value: RequirementRsValue,
    val requirement: Requirement,
    val period: Period
)
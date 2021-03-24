package com.procurement.submission.domain.rule

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails

class BidStateForSettingRule(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: Status,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails?
)

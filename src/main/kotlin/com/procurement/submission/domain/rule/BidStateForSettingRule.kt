package com.procurement.submission.domain.rule

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails

class BidStateForSettingRule(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: BidStatus,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: BidStatusDetails?
)

package com.procurement.submission.domain.rule

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.BidStatus
import com.procurement.submission.domain.model.enums.BidStatusDetails

class ValidBidStatesRule(private val states: List<State>) {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: Status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails?
    ) {

        data class Status(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: BidStatus,
        )

        data class StatusDetails(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: BidStatusDetails?
        )
    }

    fun contains(status: BidStatus, statusDetails: BidStatusDetails?): Boolean =
        states.any { state ->
            if (state.statusDetails != null)
                state.status.value == status && state.statusDetails.value == statusDetails
            else
                state.status.value == status
        }

}
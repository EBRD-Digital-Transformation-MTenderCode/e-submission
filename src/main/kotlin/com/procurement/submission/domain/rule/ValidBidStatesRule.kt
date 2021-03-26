package com.procurement.submission.domain.rule

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails

class ValidBidStatesRule(private val states: List<State>) {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ValidStatus,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ValidStatusDetails?
    ) {

        data class ValidStatus(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Status,
        )

        data class ValidStatusDetails(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: StatusDetails?
        )
    }

    fun contains(status: Status, statusDetails: StatusDetails?): Boolean =
        states.any { state ->
            if (state.statusDetails != null)
                state.status.value == status && state.statusDetails.value == statusDetails
            else
                state.status.value == status
        }

}
package com.procurement.submission.domain.rule

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.domain.model.enums.StatusDetails

class ValidBidStatesRule(states: List<State>) : List<ValidBidStatesRule.State> by states {

    data class State(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: ValidStatus,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: ValidStatusDetails?
    ) { companion object {}

        data class ValidStatus(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Status,
        )

        data class ValidStatusDetails(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: StatusDetails?
        )
    }

    override operator fun contains(element: State): Boolean =
        this.any {
            it.status.value == element.status.value &&
            it.statusDetails
                ?.let { it.value == element.statusDetails?.value }
                ?: true
        }

}

fun ValidBidStatesRule.State.Companion.from(status: Status, statusDetails: StatusDetails?): ValidBidStatesRule.State =
    ValidBidStatesRule.State(
        status = ValidBidStatesRule.State.ValidStatus(status),
        statusDetails = ValidBidStatesRule.State.ValidStatusDetails(statusDetails)
    )
package com.procurement.submission.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.application.model.data.RequirementRsValue
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.dto.ocds.Organization
import com.procurement.submission.model.dto.ocds.Period
import com.procurement.submission.model.dto.ocds.Requirement
import com.procurement.submission.model.dto.ocds.RequirementResponse
import java.time.LocalDateTime

data class GetBidsForPacsResult(
    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: Bids
) {
    data class Bids(
        @param:JsonProperty("details") @field:JsonProperty("details") val details: List<Detail>
    ) {
        data class Detail(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?
        ) {

            data class Tenderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )

            data class RequirementResponse(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementRsValue,
                @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
            ) {
                data class Requirement(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                )

                data class Period(
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
                )
            }
        }
    }

    object ResponseConverter {

        fun fromDomain(bid: Bid): Bids.Detail =
            Bids.Detail(
                id = bid.id,
                tenderers = bid.tenderers.map { fromDomain(it) },
                requirementResponses = bid.requirementResponses?.map { fromDomain(it) }
            )

        fun fromDomain(tenderer: Organization): Bids.Detail.Tenderer =
            Bids.Detail.Tenderer(id = tenderer.id!!, name = tenderer.name)

        fun fromDomain(requirementResponses: RequirementResponse): Bids.Detail.RequirementResponse =
            Bids.Detail.RequirementResponse(
                id = requirementResponses.id,
                value = requirementResponses.value,
                period = requirementResponses.period?.let { fromDomain(it) },
                requirement = fromDomain(requirementResponses.requirement)
            )

        fun fromDomain(period: Period): Bids.Detail.RequirementResponse.Period =
            Bids.Detail.RequirementResponse.Period(
                startDate = period.startDate,
                endDate = period.endDate
            )

        fun fromDomain(requirement: Requirement): Bids.Detail.RequirementResponse.Requirement =
            Bids.Detail.RequirementResponse.Requirement(id = requirement.id)

    }
}
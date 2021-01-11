package com.procurement.submission.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.submission.application.model.data.bid.get.period.GetTenderPeriodEndResult

data class GetTenderPeriodEndResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @field:JsonProperty("tenderPeriod") @param:JsonProperty("tenderPeriod") val tenderPeriod: Period
    ) {
        data class Period(
            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String
        )
    }

    companion object {
        fun fromResult(result: GetTenderPeriodEndResult): GetTenderPeriodEndResponse =
            GetTenderPeriodEndResponse(
                tender = result.tender
                    .let { tender ->
                        Tender(
                            tenderPeriod = tender.tenderPeriod
                                .let { period -> Tender.Period(endDate = period.endDate) }
                        )
                    }
            )
    }

}

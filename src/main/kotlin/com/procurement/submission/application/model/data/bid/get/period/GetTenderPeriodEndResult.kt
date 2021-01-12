package com.procurement.submission.application.model.data.bid.get.period

import com.procurement.submission.application.repository.period.model.PeriodEntity
import com.procurement.submission.infrastructure.bind.date.JsonDateSerializer

data class GetTenderPeriodEndResult(
    val tender: Tender
) {
    data class Tender(
        val tenderPeriod: Period
    ) {
        data class Period(
            val endDate: String
        )
    }

    companion object {
        fun fromDomain(periodEntity: PeriodEntity): GetTenderPeriodEndResult =
            GetTenderPeriodEndResult(
                tender = Tender(
                    tenderPeriod = periodEntity.let { period ->
                        Tender.Period(
                            endDate = JsonDateSerializer.serialize(period.endDate)
                        )
                    }
                )
            )
    }
}

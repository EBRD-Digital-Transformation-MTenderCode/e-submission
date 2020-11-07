package com.procurement.submission.infrastructure.handler.tender.period

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.repository.HistoryRepository
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.PeriodService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodRequest
import com.procurement.submission.infrastructure.dto.tender.period.set.SetTenderPeriodResult
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.AbstractHistoricalHandler2
import com.procurement.submission.infrastructure.web.response.parser.tryGetParams
import com.procurement.submission.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class SetTenderPeriodHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val periodService: PeriodService
) : AbstractHistoricalHandler2<Command2Type, SetTenderPeriodResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = SetTenderPeriodResult::class.java,
    transform = transform
) {
    override val action: Command2Type = Command2Type.SET_TENDER_PERIOD

    override fun execute(node: JsonNode): Result<SetTenderPeriodResult, Fail> {
        val params = node.tryGetParams(SetTenderPeriodRequest::class.java, transform = transform)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return periodService.setTenderPeriod(params)
    }
}
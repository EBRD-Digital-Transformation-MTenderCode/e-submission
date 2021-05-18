package com.procurement.submission.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.Logger
import com.procurement.submission.application.service.PersonsProcessingService
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.infrastructure.api.tryGetParams
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.handler.v2.model.request.FinalizeBidsByAwardsRequest
import com.procurement.submission.infrastructure.handler.v2.model.request.PersonesProcessingRequest
import com.procurement.submission.infrastructure.handler.v2.model.response.PersonesProcessingResult
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class PersonesProcessingHandler(
    logger: Logger,
    historyRepository: HistoryRepository,
    transform: Transform,
    private val personsProcessingService: PersonsProcessingService
) : AbstractHistoricalHandlerV2<CommandTypeV2, PersonesProcessingResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = PersonesProcessingResult::class.java,
    transform = transform
) {
    override val action: CommandTypeV2 = CommandTypeV2.PERSONES_PROCESSING

    override fun execute(node: JsonNode): Result<PersonesProcessingResult?, Fail> {
        val params = node.tryGetParams(PersonesProcessingRequest::class.java, transform = transform)
            .flatMap { it.convert() }
            .onFailure { return it }

        return personsProcessingService.personsProcessing(params)
    }
}
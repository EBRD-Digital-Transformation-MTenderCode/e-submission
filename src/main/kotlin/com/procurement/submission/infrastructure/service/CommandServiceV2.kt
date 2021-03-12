package com.procurement.submission.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.infrastructure.api.CommandId
import com.procurement.submission.infrastructure.api.tryGetAction
import com.procurement.submission.infrastructure.api.tryGetId
import com.procurement.submission.infrastructure.api.tryGetVersion
import com.procurement.submission.infrastructure.api.v2.ApiResponseV2
import com.procurement.submission.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.api.v2.CommandTypeV2
import com.procurement.submission.infrastructure.handler.v2.CheckAbsenceActiveInvitationsHandler
import com.procurement.submission.infrastructure.handler.v2.CheckAccessToBidHandler
import com.procurement.submission.infrastructure.handler.v2.CheckPeriodHandler
import com.procurement.submission.infrastructure.handler.v2.CreateBidHandler
import com.procurement.submission.infrastructure.handler.v2.DoInvitationsHandler
import com.procurement.submission.infrastructure.handler.v2.FindDocumentsByBidIdsHandler
import com.procurement.submission.infrastructure.handler.v2.GetBidsForPacsHandler
import com.procurement.submission.infrastructure.handler.v2.PublishInvitationsHandler
import com.procurement.submission.infrastructure.handler.v2.SetTenderPeriodHandler
import com.procurement.submission.infrastructure.handler.v2.ValidateBidDataHandler
import com.procurement.submission.infrastructure.handler.v2.ValidateTenderPeriodHandler
import org.springframework.stereotype.Service

@Service
class CommandServiceV2(
    private val logger: Logger,
    private val checkAccessToBidHandler: CheckAccessToBidHandler,
    private val doInvitationsHandler: DoInvitationsHandler,
    private val getBidsForPacsHandler: GetBidsForPacsHandler,
    private val findDocumentsByBidIdsHandler: FindDocumentsByBidIdsHandler,
    private val checkAbsenceActiveInvitationsHandler: CheckAbsenceActiveInvitationsHandler,
    private val validateBidDataHandler: ValidateBidDataHandler,
    private val createBidHandler: CreateBidHandler,
    private val validateTenderPeriodHandler: ValidateTenderPeriodHandler,
    private val setTenderPeriodHandler: SetTenderPeriodHandler,
    private val publishInvitationsHandler: PublishInvitationsHandler,
    private val checkPeriodHandler: CheckPeriodHandler
) {

    fun execute(node: JsonNode): ApiResponseV2 {

        val version = node.tryGetVersion()
            .onFailure {
                val id = node.tryGetId().getOrElse(CommandId.NaN)
                return generateResponseOnFailure(fail = it.reason, logger = logger, id = id)
            }

        val id = node.tryGetId()
            .onFailure {
                return generateResponseOnFailure(
                    fail = it.reason,
                    version = version,
                    id = CommandId.NaN,
                    logger = logger
                )
            }

        val action = node.tryGetAction()
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, id = id, version = version, logger = logger)
            }

        return when (action) {
            CommandTypeV2.CHECK_ABSENCE_ACTIVE_INVITATIONS -> checkAbsenceActiveInvitationsHandler.handle(node)
            CommandTypeV2.CHECK_ACCESS_TO_BID -> checkAccessToBidHandler.handle(node)
            CommandTypeV2.CHECK_PERIOD -> checkPeriodHandler.handle(node)
            CommandTypeV2.CREATE_BID -> createBidHandler.handle(node)
            CommandTypeV2.DO_INVITATIONS -> doInvitationsHandler.handle(node)
            CommandTypeV2.GET_BIDS_FOR_PACS -> getBidsForPacsHandler.handle(node)
            CommandTypeV2.FIND_DOCUMENTS_BY_BID_IDS -> findDocumentsByBidIdsHandler.handle(node)
            CommandTypeV2.PUBLISH_INVITATIONS -> publishInvitationsHandler.handle(node)
            CommandTypeV2.SET_TENDER_PERIOD -> setTenderPeriodHandler.handle(node)
            CommandTypeV2.VALIDATE_BID_DATA -> validateBidDataHandler.handle(node)
            CommandTypeV2.VALIDATE_TENDER_PERIOD -> validateTenderPeriodHandler.handle(node)
        }
    }
}

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
import com.procurement.submission.infrastructure.handler.v2.CheckBidStateHandler
import com.procurement.submission.infrastructure.handler.v2.CheckExistenceOfInvitationHandler
import com.procurement.submission.infrastructure.handler.v2.CheckPeriodHandler
import com.procurement.submission.infrastructure.handler.v2.CreateBidHandler
import com.procurement.submission.infrastructure.handler.v2.CreateInvitationsHandler
import com.procurement.submission.infrastructure.handler.v2.DoInvitationsHandler
import com.procurement.submission.infrastructure.handler.v2.FinalizeBidsByAwardsHandler
import com.procurement.submission.infrastructure.handler.v2.FindDocumentsByBidIdsHandler
import com.procurement.submission.infrastructure.handler.v2.GetBidsForPacsHandler
import com.procurement.submission.infrastructure.handler.v2.GetOrganizationsByReferencesFromPacsHandler
import com.procurement.submission.infrastructure.handler.v2.GetSuppliersOwnersHandler
import com.procurement.submission.infrastructure.handler.v2.PersonesProcessingHandler
import com.procurement.submission.infrastructure.handler.v2.PublishInvitationsHandler
import com.procurement.submission.infrastructure.handler.v2.SetStateForBidsHandler
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
    private val getOrganizationsByReferencesFromPacsHandler: GetOrganizationsByReferencesFromPacsHandler,
    private val findDocumentsByBidIdsHandler: FindDocumentsByBidIdsHandler,
    private val checkAbsenceActiveInvitationsHandler: CheckAbsenceActiveInvitationsHandler,
    private val validateBidDataHandler: ValidateBidDataHandler,
    private val checkBidStateHandler: CheckBidStateHandler,
    private val checkExistenceOfInvitationHandler: CheckExistenceOfInvitationHandler,
    private val createBidHandler: CreateBidHandler,
    private val createInvitationsHandler: CreateInvitationsHandler,
    private val finalizeBidsByAwardsHandler: FinalizeBidsByAwardsHandler,
    private val validateTenderPeriodHandler: ValidateTenderPeriodHandler,
    private val setStateForBidsHandler: SetStateForBidsHandler,
    private val setTenderPeriodHandler: SetTenderPeriodHandler,
    private val publishInvitationsHandler: PublishInvitationsHandler,
    private val checkPeriodHandler: CheckPeriodHandler,
    private val personesProcessingHandler: PersonesProcessingHandler,
    private val getSuppliersOwnersHandler: GetSuppliersOwnersHandler
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
            CommandTypeV2.CHECK_BID_STATE -> checkBidStateHandler.handle(node)
            CommandTypeV2.CHECK_EXISTENCE_OF_INVITATION -> checkExistenceOfInvitationHandler.handle(node)
            CommandTypeV2.CHECK_PERIOD -> checkPeriodHandler.handle(node)
            CommandTypeV2.CREATE_BID -> createBidHandler.handle(node)
            CommandTypeV2.CREATE_INVITATIONS -> createInvitationsHandler.handle(node)
            CommandTypeV2.DO_INVITATIONS -> doInvitationsHandler.handle(node)
            CommandTypeV2.GET_BIDS_FOR_PACS -> getBidsForPacsHandler.handle(node)
            CommandTypeV2.GET_ORGANIZATIONS_BY_REFERENCES_FROM_PACS -> getOrganizationsByReferencesFromPacsHandler.handle(node)
            CommandTypeV2.FINALIZE_BIDS_BY_AWARDS -> finalizeBidsByAwardsHandler.handle(node)
            CommandTypeV2.FIND_DOCUMENTS_BY_BID_IDS -> findDocumentsByBidIdsHandler.handle(node)
            CommandTypeV2.PUBLISH_INVITATIONS -> publishInvitationsHandler.handle(node)
            CommandTypeV2.SET_STATE_FOR_BIDS -> setStateForBidsHandler.handle(node)
            CommandTypeV2.SET_TENDER_PERIOD -> setTenderPeriodHandler.handle(node)
            CommandTypeV2.VALIDATE_BID_DATA -> validateBidDataHandler.handle(node)
            CommandTypeV2.VALIDATE_TENDER_PERIOD -> validateTenderPeriodHandler.handle(node)
            CommandTypeV2.PERSONES_PROCESSING -> personesProcessingHandler.handle(node)
            CommandTypeV2.GET_SUPPLIERS_OWNERS -> getSuppliersOwnersHandler.handle(node)
        }
    }
}

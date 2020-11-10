package com.procurement.submission.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.submission.application.service.Logger
import com.procurement.submission.infrastructure.enums.Command2Type
import com.procurement.submission.infrastructure.handler.bid.CreateBidHandler
import com.procurement.submission.infrastructure.handler.bid.ValidateBidDataHandler
import com.procurement.submission.infrastructure.handler.invitation.CheckAbsenceActiveInvitationsHandler
import com.procurement.submission.infrastructure.handler.invitation.DoInvitationsHandler
import com.procurement.submission.infrastructure.handler.invitation.PublishInvitationsHandler
import com.procurement.submission.infrastructure.handler.tender.period.CheckPeriodHandler
import com.procurement.submission.infrastructure.handler.tender.period.SetTenderPeriodHandler
import com.procurement.submission.infrastructure.handler.tender.period.ValidateTenderPeriodHandler
import com.procurement.submission.infrastructure.model.CommandId
import com.procurement.submission.infrastructure.web.api.response.ApiResponseV2
import com.procurement.submission.infrastructure.web.api.response.generator.ApiResponse2Generator.generateResponseOnFailure
import com.procurement.submission.infrastructure.web.response.parser.tryGetAction
import com.procurement.submission.infrastructure.web.response.parser.tryGetId
import com.procurement.submission.infrastructure.web.response.parser.tryGetVersion
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val logger: Logger,
    private val doInvitationsHandler: DoInvitationsHandler,
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
            Command2Type.CHECK_ABSENCE_ACTIVE_INVITATIONS -> checkAbsenceActiveInvitationsHandler.handle(node)
            Command2Type.CHECK_PERIOD -> checkPeriodHandler.handle(node)
            Command2Type.CREATE_BID -> createBidHandler.handle(node)
            Command2Type.DO_INVITATIONS -> doInvitationsHandler.handle(node)
            Command2Type.PUBLISH_INVITATIONS -> publishInvitationsHandler.handle(node)
            Command2Type.SET_TENDER_PERIOD -> setTenderPeriodHandler.handle(node)
            Command2Type.VALIDATE_BID_DATA -> validateBidDataHandler.handle(node)
            Command2Type.VALIDATE_TENDER_PERIOD -> validateTenderPeriodHandler.handle(node)
        }
    }
}

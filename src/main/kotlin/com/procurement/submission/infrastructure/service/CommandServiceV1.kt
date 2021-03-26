package com.procurement.submission.infrastructure.service

import com.procurement.submission.application.exception.ErrorException
import com.procurement.submission.application.exception.ErrorType
import com.procurement.submission.application.model.data.award.apply.ApplyEvaluatedAwardsContext
import com.procurement.submission.application.model.data.bid.auction.get.GetBidsAuctionContext
import com.procurement.submission.application.model.data.bid.document.open.OpenBidDocsContext
import com.procurement.submission.application.model.data.bid.get.GetBidsForEvaluationContext
import com.procurement.submission.application.model.data.bid.get.bylots.GetBidsByLotsContext
import com.procurement.submission.application.model.data.bid.get.period.GetTenderPeriodEndContext
import com.procurement.submission.application.model.data.bid.open.OpenBidsForPublishingContext
import com.procurement.submission.application.model.data.bid.status.FinalBidsStatusByLotsContext
import com.procurement.submission.application.model.data.bid.status.FinalBidsStatusByLotsData
import com.procurement.submission.application.model.data.tender.period.ExtendTenderPeriodContext
import com.procurement.submission.application.service.BidService
import com.procurement.submission.application.service.PeriodService
import com.procurement.submission.application.service.StatusService
import com.procurement.submission.domain.extension.nowDefaultUTC
import com.procurement.submission.domain.model.enums.ProcurementMethod
import com.procurement.submission.infrastructure.api.v1.CommandMessage
import com.procurement.submission.infrastructure.api.v1.CommandTypeV1
import com.procurement.submission.infrastructure.api.v1.ResponseDto
import com.procurement.submission.infrastructure.api.v1.action
import com.procurement.submission.infrastructure.api.v1.commandId
import com.procurement.submission.infrastructure.api.v1.country
import com.procurement.submission.infrastructure.api.v1.cpid
import com.procurement.submission.infrastructure.api.v1.ocid
import com.procurement.submission.infrastructure.api.v1.pmd
import com.procurement.submission.infrastructure.api.v1.startDate
import com.procurement.submission.infrastructure.handler.HistoryRepository
import com.procurement.submission.infrastructure.handler.v1.converter.convert
import com.procurement.submission.infrastructure.handler.v1.converter.toData
import com.procurement.submission.infrastructure.handler.v1.converter.toResponse
import com.procurement.submission.infrastructure.handler.v1.model.request.ApplyEvaluatedAwardsRequest
import com.procurement.submission.infrastructure.handler.v1.model.request.FinalBidsStatusByLotsRequest
import com.procurement.submission.infrastructure.handler.v1.model.request.GetBidsAuctionRequest
import com.procurement.submission.infrastructure.handler.v1.model.request.GetBidsByLotsRequest
import com.procurement.submission.infrastructure.handler.v1.model.request.GetBidsForEvaluationRequest
import com.procurement.submission.infrastructure.handler.v1.model.request.OpenBidDocsRequest
import com.procurement.submission.infrastructure.handler.v1.model.request.OpenBidsForPublishingRequest
import com.procurement.submission.infrastructure.handler.v1.model.response.FinalBidsStatusByLotsResponse
import com.procurement.submission.infrastructure.handler.v1.model.response.GetTenderPeriodEndResponse
import com.procurement.submission.infrastructure.handler.v2.converter.convert
import com.procurement.submission.infrastructure.repository.history.model.HistoryEntity
import com.procurement.submission.utils.toJson
import com.procurement.submission.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CommandServiceV1(
    private val historyDao: HistoryRepository,
    private val bidService: BidService,
    private val periodService: PeriodService,
    private val statusService: StatusService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandServiceV1::class.java)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        val history = historyDao.getHistory(cm.commandId, cm.action)
            .onFailure {
                throw RuntimeException("Error of loading history. ${it.reason.description}", it.reason.exception)
            }
        if (history != null) {
            return toObject(ResponseDto::class.java, history)
        }
        val response = when (cm.command) {
            CommandTypeV1.GET_BIDS_FOR_EVALUATION -> {
                when (cm.pmd) {
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV -> {
                        val request = toObject(GetBidsForEvaluationRequest::class.java, cm.data)
                        val requestData = request.toData()
                        val context = GetBidsForEvaluationContext(
                            cpid = cm.cpid,
                            ocid = cm.ocid,
                            country = cm.country,
                            pmd = cm.pmd
                        )
                        val serviceResponse = bidService.getBidsForEvaluation(
                            requestData = requestData,
                            context = context
                        )
                        val response = serviceResponse.toResponse()
                        return ResponseDto(data = response)
                    }

                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> throw ErrorException(ErrorType.INVALID_PMD)
                }
            }
            CommandTypeV1.GET_TENDER_PERIOD_END -> {
                val context = GetTenderPeriodEndContext(cpid = cm.cpid, ocid = cm.ocid)

                val result = periodService.getTenderPeriodEnd(context = context)
                if (log.isDebugEnabled)
                    log.debug("Result: ${toJson(result)}")

                val response = GetTenderPeriodEndResponse.fromResult(result)
                if (log.isDebugEnabled)
                    log.debug("Response: ${toJson(response)}")

                return ResponseDto(data = response)
            }
            CommandTypeV1.OPEN_BIDS_FOR_PUBLISHING -> {
                when (cm.pmd) {
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV -> {
                        val context = OpenBidsForPublishingContext(
                            cpid = cm.cpid,
                            ocid = cm.ocid
                        )
                        val request = toObject(OpenBidsForPublishingRequest::class.java, cm.data)
                        val serviceResponse = bidService.openBidsForPublishing(
                            context = context,
                            data = request.convert()
                        )
                        val response = serviceResponse.convert()
                        return ResponseDto(data = response)
                    }

                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> throw ErrorException(ErrorType.INVALID_PMD)
                }
            }
            CommandTypeV1.SAVE_PERIOD -> periodService.savePeriod(cm)
            CommandTypeV1.SAVE_NEW_PERIOD -> periodService.saveNewPeriod(cm)
            CommandTypeV1.VALIDATE_PERIOD -> periodService.periodValidation(cm)
            CommandTypeV1.CHECK_PERIOD_END_DATE -> periodService.checkEndDate(cm)
            CommandTypeV1.CHECK_PERIOD -> periodService.checkPeriod(cm)
            CommandTypeV1.CHECK_TOKEN_OWNER -> statusService.checkTokenOwner(cm)
            CommandTypeV1.GET_BIDS_AUCTION -> {
                when (cm.pmd) {
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV -> {
                        val request = toObject(GetBidsAuctionRequest::class.java, cm.data)
                        val requestData = request.convert()
                        val context = GetBidsAuctionContext(
                            cpid = cm.cpid,
                            ocid = cm.ocid,
                            country = cm.country,
                            pmd = cm.pmd
                        )
                        val serviceResponse = statusService.getBidsAuction(requestData, context)
                        val response = serviceResponse.convert()
                        return ResponseDto(data = response)
                    }

                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> throw ErrorException(ErrorType.INVALID_PMD)
                }
            }
            CommandTypeV1.UPDATE_BID_BY_AWARD_STATUS -> statusService.updateBidsByAwardStatus(cm)
            CommandTypeV1.UPDATE_BID_DOCS -> bidService.updateBidDocs(cm)
            CommandTypeV1.BID_WITHDRAWN -> statusService.bidWithdrawn(cm)
            CommandTypeV1.GET_DOCS_OF_CONSIDERED_BID -> statusService.getDocsOfConsideredBid(cm)
            CommandTypeV1.APPLY_EVALUATED_AWARDS -> {
                val context = ApplyEvaluatedAwardsContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid
                )
                val request = toObject(ApplyEvaluatedAwardsRequest::class.java, cm.data)
                val result = bidService.applyEvaluatedAwards(context = context, data = request.convert())
                if (log.isDebugEnabled)
                    log.debug("Evaluated awards were apply. Result: ${toJson(result)}")

                val dataResponse = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Evaluated awards were apply. Response: ${toJson(dataResponse)}")
                return ResponseDto(data = dataResponse)
            }
            CommandTypeV1.FINAL_BIDS_STATUS_BY_LOTS -> {
                val context = FinalBidsStatusByLotsContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    pmd = cm.pmd
                )
                val request = toObject(FinalBidsStatusByLotsRequest::class.java, cm.data)
                val data = FinalBidsStatusByLotsData(
                    lots = request.lots.map { lot ->
                        FinalBidsStatusByLotsData.Lot(
                            id = lot.id
                        )
                    }
                )

                val result = bidService.finalBidsStatusByLots(context, data)
                if (log.isDebugEnabled)
                    log.debug("Bids were finalized. Result: ${toJson(result)}")

                val dataResponse = FinalBidsStatusByLotsResponse(
                    bids = result.bids.map { bid ->
                        FinalBidsStatusByLotsResponse.Bid(
                            id = bid.id,
                            status = bid.status
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("Bids were finalized. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandTypeV1.OPEN_BID_DOCS -> {
                val context = OpenBidDocsContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid
                )
                val request = toObject(OpenBidDocsRequest::class.java, cm.data)
                val result = bidService.openBidDocs(context = context, data = request.convert())
                if (log.isDebugEnabled)
                    log.debug("Docs were opened. Result: ${toJson(result)}")
                val response = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Docs were opened. Response: ${toJson(response)}")
                ResponseDto(data = response)
            }
            CommandTypeV1.GET_BIDS_BY_LOTS -> {
                val context = GetBidsByLotsContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid
                )
                val request = toObject(GetBidsByLotsRequest::class.java, cm.data)
                val result = bidService.getBidsByLots(context = context, data = request.convert())
                if (log.isDebugEnabled)
                    log.debug("Bids are gotten. Result: ${toJson(result)}")
                val response = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Bids are gotten. Response: ${toJson(response)}")
                ResponseDto(data = response)
            }
            CommandTypeV1.EXTEND_TENDER_PERIOD -> {
                val context = ExtendTenderPeriodContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    startDate = cm.startDate,
                    country = cm.country,
                    pmd = cm.pmd
                )
                val result = periodService.extendTenderPeriod(context)
                if (log.isDebugEnabled)
                    log.debug("Extended tender period. Result: ${toJson(result)}")

                val response = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Extended tender period. Response: ${toJson(response)}")
                ResponseDto(data = response)
            }
        }
        val historyEntity = HistoryEntity(
            commandId = cm.commandId,
            action = cm.action,
            date = nowDefaultUTC(),
            data = toJson(response)
        )
        historyDao.saveHistory(historyEntity)
            .doOnError {
                log.error("Error of save history. ${it.description}", it.exception)
            }
        return toObject(ResponseDto::class.java, historyEntity.data)
    }
}

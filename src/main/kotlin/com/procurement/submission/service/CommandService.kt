package com.procurement.submission.service

import com.procurement.submission.application.service.ApplyEvaluatedAwardsContext
import com.procurement.submission.application.service.ApplyEvaluatedAwardsData
import com.procurement.submission.application.service.BidCreateContext
import com.procurement.submission.application.service.BidUpdateContext
import com.procurement.submission.application.service.FinalBidsStatusByLotsContext
import com.procurement.submission.application.service.FinalBidsStatusByLotsData
import com.procurement.submission.application.service.GetBidsAuctionContext
import com.procurement.submission.application.service.GetBidsForEvaluationContext
import com.procurement.submission.application.service.OpenBidsForPublishingContext
import com.procurement.submission.application.service.bid.bidsbylots.GetBidsByLotsContext
import com.procurement.submission.application.service.bid.opendoc.OpenBidDocsContext
import com.procurement.submission.dao.HistoryDao
import com.procurement.submission.domain.model.ProcurementMethod
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.infrastructure.converter.convert
import com.procurement.submission.infrastructure.converter.toData
import com.procurement.submission.infrastructure.converter.toResponse
import com.procurement.submission.infrastructure.dto.award.EvaluatedAwardsRequest
import com.procurement.submission.infrastructure.dto.award.EvaluatedAwardsResponse
import com.procurement.submission.infrastructure.dto.bid.bidsbylots.request.GetBidsByLotsRequest
import com.procurement.submission.infrastructure.dto.bid.finalize.request.FinalBidsStatusByLotsRequest
import com.procurement.submission.infrastructure.dto.bid.finalize.response.FinalBidsStatusByLotsResponse
import com.procurement.submission.infrastructure.dto.bid.opendoc.request.OpenBidDocsRequest
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.CommandType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.bpe.country
import com.procurement.submission.model.dto.bpe.cpid
import com.procurement.submission.model.dto.bpe.ctxId
import com.procurement.submission.model.dto.bpe.owner
import com.procurement.submission.model.dto.bpe.pmd
import com.procurement.submission.model.dto.bpe.stage
import com.procurement.submission.model.dto.bpe.startDate
import com.procurement.submission.model.dto.bpe.token
import com.procurement.submission.model.dto.request.BidCreateRequest
import com.procurement.submission.model.dto.request.BidUpdateRequest
import com.procurement.submission.model.dto.request.GetBidsAuctionRequest
import com.procurement.submission.model.dto.request.GetBidsForEvaluationRequest
import com.procurement.submission.model.dto.request.OpenBidsForPublishingRequest
import com.procurement.submission.utils.toJson
import com.procurement.submission.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CommandService(
    private val historyDao: HistoryDao,
    private val bidService: BidService,
    private val periodService: PeriodService,
    private val statusService: StatusService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CREATE_BID -> {
                when (cm.pmd) {
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV -> {
                        val request = toObject(BidCreateRequest::class.java, cm.data)
                        val requestData = request.toData()
                        val context = BidCreateContext(
                            cpid = cm.cpid,
                            owner = cm.owner,
                            stage = cm.stage,
                            startDate = cm.startDate
                        )
                        bidService.createBid(requestData = requestData, context = context)
                    }

                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> {
                        throw ErrorException(ErrorType.INVALID_PMD)
                    }

                }
            }
            CommandType.UPDATE_BID -> {
                when (cm.pmd) {
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV -> {
                        val request = toObject(BidUpdateRequest::class.java, cm.data)
                        val requestData = request.toData()
                        val context = BidUpdateContext(
                            id = cm.ctxId,
                            cpid = cm.cpid,
                            owner = cm.owner,
                            stage = cm.stage,
                            token = cm.token,
                            startDate = cm.startDate
                        )
                        bidService.updateBid(requestData = requestData, context = context)
                    }

                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> {
                        throw ErrorException(ErrorType.INVALID_PMD)
                    }

                }
            }
            CommandType.GET_BIDS_FOR_EVALUATION -> {
                when (cm.pmd) {
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV -> {
                        val request = toObject(GetBidsForEvaluationRequest::class.java, cm.data)
                        val requestData = request.toData()
                        val context = GetBidsForEvaluationContext(
                            cpid = cm.cpid,
                            stage = cm.stage,
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

                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> {
                        throw ErrorException(ErrorType.INVALID_PMD)
                    }

                }
            }
            CommandType.OPEN_BIDS_FOR_PUBLISHING -> {
                when (cm.pmd) {
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV -> {
                        val context = OpenBidsForPublishingContext(
                            cpid = cm.cpid,
                            stage = cm.stage
                        )
                        val request = toObject(OpenBidsForPublishingRequest::class.java, cm.data)
                        val serviceResponse = bidService.openBidsForPublishing(
                            context = context,
                            data = request.convert()
                        )
                        val response = serviceResponse.convert()
                        return ResponseDto(data = response)
                    }

                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> {
                        throw ErrorException(ErrorType.INVALID_PMD)
                    }

                }
            }
            CommandType.COPY_BIDS -> bidService.copyBids(cm)
            CommandType.GET_PERIOD -> periodService.getPeriod(cm)
            CommandType.SAVE_PERIOD -> periodService.savePeriod(cm)
            CommandType.SAVE_NEW_PERIOD -> periodService.saveNewPeriod(cm)
            CommandType.VALIDATE_PERIOD -> periodService.periodValidation(cm)
            CommandType.CHECK_PERIOD_END_DATE -> periodService.checkEndDate(cm)
            CommandType.CHECK_PERIOD -> periodService.checkPeriod(cm)
            CommandType.CHECK_TOKEN_OWNER -> statusService.checkTokenOwner(cm)
            CommandType.GET_BIDS -> statusService.getBids(cm)
            CommandType.GET_BIDS_AUCTION -> {
                when (cm.pmd) {
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV -> {
                        val request = toObject(GetBidsAuctionRequest::class.java, cm.data)
                        val requestData = request.convert()
                        val context = GetBidsAuctionContext(
                            cpid = cm.cpid,
                            stage = cm.stage,
                            country = cm.country,
                            pmd = cm.pmd
                        )
                        val serviceResponse = statusService.getBidsAuction(requestData, context)
                        val response = serviceResponse.convert()
                        return ResponseDto(data = response)
                    }

                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> {
                        throw ErrorException(ErrorType.INVALID_PMD)
                    }

                }
            }
            CommandType.UPDATE_BIDS_BY_LOTS -> statusService.updateBidsByLots(cm)
            CommandType.UPDATE_BID_BY_AWARD_STATUS -> statusService.updateBidsByAwardStatus(cm)
            CommandType.UPDATE_BID_DOCS -> bidService.updateBidDocs(cm)
            CommandType.SET_BIDS_FINAL_STATUSES -> statusService.setFinalStatuses(cm)
            CommandType.BID_WITHDRAWN -> statusService.bidWithdrawn(cm)
            CommandType.PREPARE_BIDS_CANCELLATION -> statusService.prepareBidsCancellation(cm)
            CommandType.BIDS_CANCELLATION -> statusService.bidsCancellation(cm)
            CommandType.GET_DOCS_OF_CONSIDERED_BID -> statusService.getDocsOfConsideredBid(cm)
            CommandType.SET_INITIAL_BIDS_STATUS -> bidService.setInitialBidsStatus(cm)
            CommandType.APPLY_EVALUATED_AWARDS -> {
                val context = ApplyEvaluatedAwardsContext(
                    cpid = cm.cpid,
                    stage = cm.stage
                )
                val request = toObject(EvaluatedAwardsRequest::class.java, cm.data)
                val data = ApplyEvaluatedAwardsData(
                    awards = request.awards.map { award ->
                        ApplyEvaluatedAwardsData.Award(
                            statusDetails = award.statusDetails,
                            relatedBid = award.relatedBid
                        )
                    }
                )
                val result = bidService.applyEvaluatedAwards(context = context, data = data)
                if (log.isDebugEnabled)
                    log.debug("Evaluated awards were apply. Result: ${toJson(result)}")

                val dataResponse = EvaluatedAwardsResponse(
                    bids = result.bids.map { bid ->
                        EvaluatedAwardsResponse.Bid(
                            id = bid.id,
                            statusDetails = bid.statusDetails
                        )
                    }
                )
                return ResponseDto(data = dataResponse)
            }
            CommandType.FINAL_BIDS_STATUS_BY_LOTS -> {
                val context = FinalBidsStatusByLotsContext(
                    cpid = cm.cpid,
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
                            status = bid.status,
                            statusDetails = bid.statusDetails
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("Bids were finalized. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.OPEN_BID_DOCS -> {
                val context = OpenBidDocsContext(cpid = cm.cpid, stage = cm.stage)
                val request = toObject(OpenBidDocsRequest::class.java, cm.data)
                val result = bidService.openBidDocs(context = context, data = request.convert())
                if (log.isDebugEnabled)
                    log.debug("Docs were opened. Result: ${toJson(result)}")
                val response = result.convert()
                if (log.isDebugEnabled)
                    log.debug("Docs were opened. Response: ${toJson(response)}")
                ResponseDto(data = response)
            }
            CommandType.GET_BIDS_BY_LOTS -> {
                val context = GetBidsByLotsContext(
                    cpid = cm.cpid,
                    stage = cm.stage
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
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }
}

package com.procurement.submission.service

import com.procurement.submission.dao.HistoryDao
import com.procurement.submission.model.dto.bpe.CommandMessage
import com.procurement.submission.model.dto.bpe.CommandType
import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.utils.toObject
import org.springframework.stereotype.Service

interface CommandService {

    fun execute(cm: CommandMessage): ResponseDto

}

@Service
class CommandServiceImpl(private val historyDao: HistoryDao,
                         private val bidService: BidService,
                         private val periodService: PeriodService,
                         private val statusService: StatusService) : CommandService {


    override fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.context.operationId, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CREATE_BID -> bidService.createBid(cm)
            CommandType.UPDATE_BID -> bidService.updateBid(cm)
            CommandType.COPY_BIDS -> bidService.copyBids(cm)
            CommandType.GET_PERIOD -> periodService.getPeriod(cm)
            CommandType.SAVE_PERIOD -> periodService.savePeriod(cm)
            CommandType.SAVE_NEW_PERIOD -> periodService.saveNewPeriod(cm)
            CommandType.VALIDATE_PERIOD -> periodService.periodValidation(cm)
            CommandType.CHECK_PERIOD_END_DATE -> periodService.checkEndDate(cm)
            CommandType.CHECK_PERIOD -> periodService.checkPeriod(cm)
            CommandType.BIDS_SELECTION -> statusService.bidsSelection(cm)
            CommandType.UPDATE_BIDS_BY_LOTS -> statusService.updateBidsByLots(cm)
            CommandType.UPDATE_BID_BY_AWARD_STATUS -> statusService.updateBidsByAwardStatus(cm)
            CommandType.SET_BIDS_FINAL_STATUSES -> statusService.setFinalStatuses(cm)
            CommandType.BID_WITHDRAWN -> statusService.bidWithdrawn(cm)
            CommandType.BIDS_WITHDRAWN -> statusService.bidsWithdrawn(cm)
            CommandType.PREPARE_BIDS_CANCELLATION -> statusService.prepareBidsCancellation(cm)
            CommandType.BIDS_CANCELLATION -> statusService.bidsSelection(cm)
        }
        historyEntity = historyDao.saveHistory(cm.context.operationId, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }
}
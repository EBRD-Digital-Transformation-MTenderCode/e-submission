package com.procurement.submission.controller

import com.procurement.submission.exception.EnumException
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.model.dto.bpe.*
import com.procurement.submission.service.BidService
import com.procurement.submission.service.PeriodService
import com.procurement.submission.service.StatusService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/command")
class CommandController(private val bidService: BidService,
                        private val periodService: PeriodService,
                        private val statusService: StatusService) {

    @PostMapping
    fun command(@RequestBody commandMessage: CommandMessage): ResponseEntity<ResponseDto> {
        return ResponseEntity(execute(commandMessage), HttpStatus.OK)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        return when (cm.command) {
            CommandType.CREATE_BID -> bidService.createBid(cm)
            CommandType.UPDATE_BID -> bidService.updateBid(cm)
            CommandType.COPY_BIDS -> bidService.copyBids(cm)
            CommandType.GET_PERIOD -> periodService.getPeriod(cm)
            CommandType.SAVE_PERIOD -> periodService.savePeriod(cm)
            CommandType.SAVE_NEW_PERIOD -> periodService.saveNewPeriod(cm)
            CommandType.PERIOD_VALIDATION -> periodService.periodValidation(cm)
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
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        return when (ex) {
            is ErrorException -> getErrorExceptionResponseDto(ex)
            is EnumException -> getEnumExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }
}




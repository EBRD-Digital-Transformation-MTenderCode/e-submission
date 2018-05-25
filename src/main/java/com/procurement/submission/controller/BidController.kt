package com.procurement.submission.controller

import com.procurement.notice.model.bpe.ResponseDto
import com.procurement.submission.model.dto.request.BidRequestDto
import com.procurement.submission.model.dto.request.LotsDto
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto
import com.procurement.submission.model.ocds.AwardStatusDetails
import com.procurement.submission.service.BidService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.validation.Valid

@Validated
@RestController
@RequestMapping(path = ["/submission"])
class BidController(private val bidService: BidService) {

    @PostMapping(value = "/bid")
    fun createBid(@RequestParam("identifier") cpId: String,
                  @RequestParam("stage") stage: String,
                  @RequestParam("owner") owner: String,
                  @Valid @RequestBody data: BidRequestDto): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.createBid(
                        cpId = cpId,
                        stage = stage,
                        owner = owner,
                        bid = data.bid),
                HttpStatus.CREATED)
    }

    @PutMapping(value = "/bid")
    fun updateBid(@RequestParam("identifier") cpId: String,
                  @RequestParam("stage") stage: String,
                  @RequestParam("token") token: String,
                  @RequestParam("owner") owner: String,
                  @Valid @RequestBody data: BidRequestDto): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.updateBid(
                        cpId = cpId,
                        stage = stage,
                        token = token,
                        owner = owner,
                        bid = data.bid),
                HttpStatus.OK)
    }

    @PostMapping(value = "/copyBids")
    fun copyBids(@RequestParam("identifier") cpId: String,
                 @RequestParam("stage") newStage: String,
                 @RequestParam("previousStage") previousStage: String,
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                 @RequestParam("startDate") startDate: LocalDateTime,
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                 @RequestParam("endDate") endDate: LocalDateTime,
                 @Valid @RequestBody data: LotsDto): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.copyBids(
                        cpId = cpId,
                        newStage = newStage,
                        previousStage = previousStage,
                        startDate = startDate,
                        endDate = endDate,
                        lots = data),
                HttpStatus.OK)
    }

    @GetMapping(value = "/bids")
    fun getPendingBids(@RequestParam("identifier") cpId: String,
                       @RequestParam("stage") stage: String,
                       @RequestParam("country") country: String,
                       @RequestParam("pmd") pmd: String): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.getPendingBids(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd),
                HttpStatus.OK)
    }

    @PostMapping(value = "/updateStatus")
    fun updateStatus(@RequestParam("identifier") cpId: String,
                     @RequestParam("stage") stage: String,
                     @RequestParam("country") country: String,
                     @RequestParam("pmd") pmd: String,
                     @RequestBody data: UnsuccessfulLotsDto): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.updateStatus(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd,
                        unsuccessfulLots = data),
                HttpStatus.OK)
    }

    @PostMapping(value = "/updateStatusDetails")
    fun updateStatusDetails(@RequestParam("identifier") cpId: String,
                            @RequestParam("stage") stage: String,
                            @RequestParam("bidId") bidId: String,
                            @RequestParam("awardStatusDetails") awardStatusDetails: String): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.updateStatusDetails(
                        cpId = cpId,
                        stage = stage,
                        bidId = bidId,
                        awardStatusDetails = AwardStatusDetails.fromValue(awardStatusDetails)),
                HttpStatus.OK)
    }

    @PostMapping(value = "/setFinalStatuses")
    fun setFinalStatuses(@RequestParam("identifier") cpId: String,
                         @RequestParam("stage") stage: String,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                         @RequestParam("date")
                         dateTime: LocalDateTime): ResponseEntity<ResponseDto<*>> {
        return ResponseEntity(
                bidService.setFinalStatuses(
                        cpId = cpId,
                        stage = stage,
                        dateTime = dateTime),
                HttpStatus.OK)
    }
}

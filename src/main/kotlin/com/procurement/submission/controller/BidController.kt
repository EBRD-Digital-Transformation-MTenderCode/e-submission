package com.procurement.submission.controller

import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.AwardStatusDetails
import com.procurement.submission.model.dto.request.BidCreateDto
import com.procurement.submission.model.dto.request.BidUpdateDto
import com.procurement.submission.model.dto.request.LotsDto
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto
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

    @PostMapping("/bid")
    fun createBid(@RequestParam("cpid") cpId: String,
                  @RequestParam("stage") stage: String,
                  @RequestParam("owner") owner: String,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                  @RequestParam("date") dateTime: LocalDateTime,
                  @Valid @RequestBody data: BidCreateDto): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                bidService.createBid(
                        cpId = cpId,
                        stage = stage,
                        owner = owner,
                        dateTime = dateTime,
                        bidDto = data.bid),
                HttpStatus.CREATED)
    }

    @PutMapping("/bid")
    fun updateBid(@RequestParam("cpid") cpId: String,
                  @RequestParam("stage") stage: String,
                  @RequestParam("owner") owner: String,
                  @RequestParam("token") token: String,
                  @RequestParam("bidId") bidId: String,
                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                  @RequestParam("date") dateTime: LocalDateTime,
                  @Valid @RequestBody data: BidUpdateDto): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                bidService.updateBid(
                        cpId = cpId,
                        stage = stage,
                        owner = owner,
                        token = token,
                        bidId = bidId,
                        dateTime = dateTime,
                        bidDto = data.bid),
                HttpStatus.OK)
    }

    @PostMapping("/copyBids")
    fun copyBids(@RequestParam("cpid") cpId: String,
                 @RequestParam("stage") newStage: String,
                 @RequestParam("previousStage") previousStage: String,
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                 @RequestParam("startDate") startDate: LocalDateTime,
                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                 @RequestParam("endDate") endDate: LocalDateTime,
                 @Valid @RequestBody data: LotsDto): ResponseEntity<ResponseDto> {
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

    @GetMapping("/bids")
    fun getPendingBids(@RequestParam("cpid") cpId: String,
                       @RequestParam("stage") stage: String,
                       @RequestParam("country") country: String,
                       @RequestParam("pmd") pmd: String): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                bidService.getPendingBids(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd),
                HttpStatus.OK)
    }

    @PostMapping("/updateStatus")
    fun updateStatus(@RequestParam("cpid") cpId: String,
                     @RequestParam("stage") stage: String,
                     @RequestParam("country") country: String,
                     @RequestParam("pmd") pmd: String,
                     @RequestBody data: UnsuccessfulLotsDto): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                bidService.updateStatus(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd,
                        unsuccessfulLots = data),
                HttpStatus.OK)
    }

    @PostMapping("/updateStatusDetails")
    fun updateStatusDetails(@RequestParam("cpid") cpId: String,
                            @RequestParam("stage") stage: String,
                            @RequestParam("bidId") bidId: String,
                            @RequestParam("awardStatusDetails") awardStatusDetails: String): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                bidService.updateStatusDetails(
                        cpId = cpId,
                        stage = stage,
                        bidId = bidId,
                        awardStatusDetails = AwardStatusDetails.fromValue(awardStatusDetails)),
                HttpStatus.OK)
    }

    @PostMapping("/setFinalStatuses")
    fun setFinalStatuses(@RequestParam("cpid") cpId: String,
                         @RequestParam("stage") stage: String,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                         @RequestParam("date")
                         dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                bidService.setFinalStatuses(
                        cpId = cpId,
                        stage = stage,
                        dateTime = dateTime),
                HttpStatus.OK)
    }
}

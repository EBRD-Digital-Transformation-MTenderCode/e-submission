package com.procurement.submission.controller

import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.model.dto.ocds.AwardStatusDetails
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto
import com.procurement.submission.service.StatusService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@Validated
@RestController
@RequestMapping(path = ["/submission"])
class StatusController(private val statusService: StatusService) {

    @GetMapping("/bidsSelection")
    fun bidsSelection(@RequestParam("cpid") cpId: String,
                      @RequestParam("stage") stage: String,
                      @RequestParam("country") country: String,
                      @RequestParam("pmd") pmd: String,
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                      @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                statusService.bidsSelection(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd,
                        dateTime = dateTime),
                HttpStatus.OK)
    }

    @PostMapping("/updateStatus")
    fun updateStatus(@RequestParam("cpid") cpId: String,
                     @RequestParam("stage") stage: String,
                     @RequestParam("country") country: String,
                     @RequestParam("pmd") pmd: String,
                     @RequestBody data: UnsuccessfulLotsDto): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                statusService.updateStatus(
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
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                            @RequestParam("date") dateTime: LocalDateTime,
                            @RequestParam("awardStatusDetails") awardStatusDetails: String): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                statusService.updateStatusDetails(
                        cpId = cpId,
                        stage = stage,
                        bidId = bidId,
                        dateTime = dateTime,
                        awardStatusDetails = AwardStatusDetails.fromValue(awardStatusDetails)),
                HttpStatus.OK)
    }

    @PostMapping("/setFinalStatuses")
    fun setFinalStatuses(@RequestParam("cpid") cpId: String,
                         @RequestParam("stage") stage: String,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                         @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                statusService.setFinalStatuses(
                        cpId = cpId,
                        stage = stage,
                        dateTime = dateTime),
                HttpStatus.OK)
    }

    @PostMapping("/bidsWithdrawn")
    fun bidsWithdrawn(@RequestParam("cpid") cpId: String,
                      @RequestParam("stage") stage: String,
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                      @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                statusService.bidsWithdrawn(
                        cpId = cpId,
                        stage = stage,
                        dateTime = dateTime),
                HttpStatus.OK)
    }

    @PostMapping("/bidWithdrawn")
    fun bidWithdrawn(@RequestParam("cpid") cpId: String,
                    @RequestParam("stage") stage: String,
                    @RequestParam("token") token: String,
                    @RequestParam("owner") owner: String,
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    @RequestParam("date") dateTime: LocalDateTime,
                    @RequestParam("bidId") bidId: String): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                statusService.bidWithdrawn(
                        cpId = cpId,
                        stage = stage,
                        owner = owner,
                        token = token,
                        bidId = bidId,
                        dateTime = dateTime),
                HttpStatus.OK)

    }
}

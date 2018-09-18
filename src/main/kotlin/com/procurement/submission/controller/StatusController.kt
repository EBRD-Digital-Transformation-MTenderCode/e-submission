package com.procurement.submission.controller

import com.procurement.submission.service.StatusService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping(path = ["/submission"])
class StatusController(private val statusService: StatusService) {

//    @GetMapping("/bidsSelection")
//    fun bidsSelection(@RequestParam("cpid") cpId: String,
//                      @RequestParam("stage") stage: String,
//                      @RequestParam("country") country: String,
//                      @RequestParam("pmd") pmd: String,
//                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                      @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.bidsSelection(
//                        cpId = cpId,
//                        stage = stage,
//                        country = country,
//                        pmd = pmd,
//                        dateTime = dateTime),
//                HttpStatus.OK)
//    }

//    @PostMapping("/updateBidsByLots")
//    fun updateBidsByLots(@RequestParam("cpid") cpId: String,
//                     @RequestParam("stage") stage: String,
//                     @RequestParam("country") country: String,
//                     @RequestParam("pmd") pmd: String,
//                     @RequestBody data: UpdateBidsByLotsRq): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.updateBidsByLots(
//                        cpId = cpId,
//                        stage = stage,
//                        country = country,
//                        pmd = pmd,
//                        unsuccessfulLots = data),
//                HttpStatus.OK)
//    }

//    @PostMapping("/updateBidsByAwardStatus")
//    fun updateBidsByAwardStatus(@RequestParam("cpid") cpId: String,
//                            @RequestParam("stage") stage: String,
//                            @RequestParam("bidId") bidId: String,
//                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                            @RequestParam("date") dateTime: LocalDateTime,
//                            @RequestParam("awardStatusDetails") awardStatusDetails: String): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.updateBidsByAwardStatus(
//                        cpId = cpId,
//                        stage = stage,
//                        bidId = bidId,
//                        dateTime = dateTime,
//                        awardStatusDetails = AwardStatusDetails.fromValue(awardStatusDetails)),
//                HttpStatus.OK)
//    }

//    @PostMapping("/setFinalStatuses")
//    fun setFinalStatuses(@RequestParam("cpid") cpId: String,
//                         @RequestParam("stage") stage: String,
//                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                         @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.setFinalStatuses(
//                        cpId = cpId,
//                        stage = stage,
//                        dateTime = dateTime),
//                HttpStatus.OK)
//    }

//    @PostMapping("/bidsWithdrawn")
//    fun bidsWithdrawn(@RequestParam("cpid") cpId: String,
//                      @RequestParam("stage") stage: String,
//                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                      @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.bidsWithdrawn(
//                        cpId = cpId,
//                        stage = stage,
//                        dateTime = dateTime),
//                HttpStatus.OK)
//    }

//    @PostMapping("/bidWithdrawn")
//    fun bidWithdrawn(@RequestParam("cpid") cpId: String,
//                     @RequestParam("stage") stage: String,
//                     @RequestParam("token") token: String,
//                     @RequestParam("owner") owner: String,
//                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                     @RequestParam("date") dateTime: LocalDateTime,
//                     @RequestParam("bidId") bidId: String): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.bidWithdrawn(
//                        cpId = cpId,
//                        stage = stage,
//                        owner = owner,
//                        token = token,
//                        bidId = bidId,
//                        dateTime = dateTime),
//                HttpStatus.OK)
//    }

//    @PostMapping("/prepareBidsCancellation")
//    fun prepareBidsCancellation(@RequestParam("cpid") cpId: String,
//                            @RequestParam("stage") stage: String,
//                            @RequestParam("pmd") pmd: String,
//                            @RequestParam("phase") phase: String,
//                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                            @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.prepareBidsCancellation(
//                        cpId = cpId,
//                        stage = stage,
//                        pmd = pmd,
//                        phase = phase,
//                        dateTime = dateTime),
//                HttpStatus.OK)
//    }
//
//    @PostMapping("/bidsCancellation")
//    fun bidsCancellation(@RequestParam("cpid") cpId: String,
//                         @RequestParam("stage") stage: String,
//                         @RequestParam("pmd") pmd: String,
//                         @RequestParam("phase") phase: String,
//                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                         @RequestParam("date") dateTime: LocalDateTime): ResponseEntity<ResponseDto> {
//        return ResponseEntity(
//                statusService.bidsCancellation(
//                        cpId = cpId,
//                        stage = stage,
//                        pmd = pmd,
//                        phase = phase,
//                        dateTime = dateTime),
//                HttpStatus.OK)
//    }
}

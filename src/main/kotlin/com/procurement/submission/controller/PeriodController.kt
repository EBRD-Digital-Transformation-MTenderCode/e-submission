package com.procurement.submission.controller

import com.procurement.submission.model.dto.bpe.ResponseDto
import com.procurement.submission.service.PeriodService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping(path = ["/period"])
class PeriodController(private val periodService: PeriodService) {


    @GetMapping
    fun getPeriod(@RequestParam("cpid") cpId: String,
                  @RequestParam("stage") stage: String): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                periodService.getPeriod(cpId = cpId, stage = stage),
                HttpStatus.CREATED)
    }

    @PostMapping("/save")
    fun savePeriod(@RequestParam("cpid") cpId: String,
                   @RequestParam("stage") stage: String,
                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                   @RequestParam("startDate") startDate: LocalDateTime,
                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                   @RequestParam("endDate") endDate: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                periodService.savePeriod(
                        cpId = cpId,
                        stage = stage,
                        startDate = startDate,
                        endDate = endDate),
                HttpStatus.CREATED)
    }

    @PostMapping("/new")
    fun saveNewPeriod(@RequestParam("cpid") cpId: String,
                      @RequestParam("stage") stage: String,
                      @RequestParam("country") country: String,
                      @RequestParam("pmd") pmd: String,
                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                      @RequestParam("startDate") startDate: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                periodService.saveNewPeriod(
                        cpId = cpId,
                        stage = stage,
                        country = country,
                        pmd = pmd,
                        startDate = startDate),
                HttpStatus.CREATED)
    }

    @PostMapping("/validation")
    fun periodValidation(@RequestParam("country") country: String,
                         @RequestParam("pmd") pmd: String,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                         @RequestParam("startDate") startDate: LocalDateTime,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                         @RequestParam("endDate") endDate: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                periodService.periodValidation(
                        country = country,
                        pmd = pmd,
                        startDate = startDate,
                        endDate = endDate),
                HttpStatus.OK)
    }

    @PostMapping("/check")
    fun checkPeriod(@RequestParam("cpid") cpId: String,
                    @RequestParam("stage") stage: String,
                    @RequestParam("country") country: String,
                    @RequestParam("pmd") pmd: String,
                    @RequestParam("operationType") operationType: String,
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    @RequestParam("startDate") startDate: LocalDateTime,
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    @RequestParam("endDate") endDate: LocalDateTime): ResponseEntity<ResponseDto> {
        return ResponseEntity(
                periodService.checkPeriod(
                        cpId = cpId,
                        country = country,
                        pmd = pmd,
                        operationType = operationType,
                        stage = stage,
                        requestDate = startDate,
                        endDateReq = endDate),
                HttpStatus.OK)
    }
}

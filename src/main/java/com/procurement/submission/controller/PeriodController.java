package com.procurement.submission.controller;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.service.PeriodService;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/period")
public class PeriodController {

    private PeriodService periodService;

    public PeriodController(final PeriodService periodService) {
        this.periodService = periodService;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDto> savePeriod(@RequestParam("identifier") final String cpId,
                                                  @RequestParam("stage") final String stage,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam("startDate") final LocalDateTime startDate,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam("endDate") final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.savePeriod(cpId, stage, startDate, endDate), HttpStatus.CREATED);
    }

    @PostMapping("/new")
    public ResponseEntity<ResponseDto> saveNewPeriod(@RequestParam("identifier") final String cpId,
                                                     @RequestParam("stage") final String stage,
                                                     @RequestParam("country") final String country,
                                                     @RequestParam("pmd") final String pmd,
                                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                     @RequestParam("startDate") final LocalDateTime startDate) {
        return new ResponseEntity<>(periodService.saveNewPeriod(cpId, stage, country, pmd, startDate), HttpStatus
                .CREATED);
    }

    @PostMapping("/validation")
    public ResponseEntity<ResponseDto> periodValidation(@RequestParam("country") final String country,
                                                        @RequestParam("pmd") final String pmd,
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                        @RequestParam("startDate") final LocalDateTime startDate,
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                        @RequestParam("endDate") final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.periodValidation(country, pmd, startDate, endDate),
                HttpStatus.OK);
    }

    @PostMapping("/check")
    public ResponseEntity<ResponseDto> checkPeriod(@RequestParam("identifier") final String cpId,
                                                   @RequestParam("stage") final String stage,
                                                   @RequestParam("country") final String country,
                                                   @RequestParam("pmd") final String pmd,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam("startDate") final LocalDateTime startDate,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam("endDate") final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.checkPeriod(cpId, country, pmd, stage, startDate, endDate),
                HttpStatus.OK);
    }
}

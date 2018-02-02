package com.procurement.submission.controller;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.service.PeriodService;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/period")
public class PeriodController {

    private PeriodService periodService;

    public PeriodController(final PeriodService periodService) {
        this.periodService = periodService;
    }

    @PostMapping("/check")
    public ResponseEntity<ResponseDto> checkPeriod(@RequestParam final String country,
                                                   @RequestParam final String pmd,
                                                   @RequestParam final String stage,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam final LocalDateTime startDate,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.checkInterval(country, pmd, stage, startDate, endDate), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDto> savePeriod(@RequestParam final String cpId,
                                                  @RequestParam final String stage,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam final LocalDateTime startDate,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.savePeriod(cpId, stage, startDate, endDate), HttpStatus.CREATED);
    }
}

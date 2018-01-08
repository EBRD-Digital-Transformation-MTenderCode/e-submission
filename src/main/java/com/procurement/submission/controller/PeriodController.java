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
    public ResponseEntity<ResponseDto> checkPeriod(@RequestParam("country") final String country,
                                                   @RequestParam("pmd") final String pmd,
                                                   @RequestParam("stage") final String stage,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam("startDate") final LocalDateTime startDate,
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   @RequestParam("endDate") final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.checkPeriod(country, pmd, stage, startDate, endDate), HttpStatus.OK);
    }

    @PostMapping("/save")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> savePeriod(@RequestParam("cpid") final String cpid,
                                                  @RequestParam("stage") final String stage,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam("startDate") final LocalDateTime startDate,
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                  @RequestParam("endDate") final LocalDateTime endDate) {
        return new ResponseEntity<>(periodService.savePeriod(cpid, stage, startDate, endDate), HttpStatus.OK);
    }
}

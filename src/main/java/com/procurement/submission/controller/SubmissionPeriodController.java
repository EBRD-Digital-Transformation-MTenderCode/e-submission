package com.procurement.submission.controller;

import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.service.PeriodService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/period")
public class SubmissionPeriodController {

    private PeriodService submissionPeriodService;

    public SubmissionPeriodController(PeriodService submissionPeriodService) {
        this.submissionPeriodService = submissionPeriodService;
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkPeriod(@Valid @RequestBody final PeriodDataDto dataDto,
                                               final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        Boolean isValid = submissionPeriodService.checkPeriod(dataDto);
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }

    @PostMapping("/save")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void savePeriod(@Valid @RequestBody final PeriodDataDto dataDto,
                                          final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        submissionPeriodService.insertData(dataDto);
    }

}

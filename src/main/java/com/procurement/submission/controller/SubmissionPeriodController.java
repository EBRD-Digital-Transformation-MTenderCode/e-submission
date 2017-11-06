package com.procurement.submission.controller;

import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.request.SubmissionPeriodDto;
import com.procurement.submission.service.SubmissionPeriodService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/period")
public class SubmissionPeriodController {

    private SubmissionPeriodService submissionPeriodService;

    public SubmissionPeriodController(SubmissionPeriodService submissionPeriodService) {
        this.submissionPeriodService = submissionPeriodService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveSubmissionPeriod(@Valid @RequestBody final SubmissionPeriodDto dataDto,
                                          final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        submissionPeriodService.insertData(dataDto);
    }
}

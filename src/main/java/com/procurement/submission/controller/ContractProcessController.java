package com.procurement.submission.controller;

import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.ContractProcessPeriodDto;
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
public class ContractProcessController {

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveContractProcessPeriod(@Valid @RequestBody final ContractProcessPeriodDto contractProcessPeriodDto,
                                          final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }
}

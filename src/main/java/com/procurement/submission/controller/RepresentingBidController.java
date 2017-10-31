package com.procurement.submission.controller;

import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.BidsDto;
import com.procurement.submission.model.dto.DocumentBidSubmissionDto;
import com.procurement.submission.model.dto.ValueDto;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepresentingBidController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void submissionQualificationProposal(@Valid @RequestBody final BidsDto bidsDto,
                                                final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void submissionTechnicalProposal(@Valid @RequestBody final DocumentBidSubmissionDto documentBidSubmissionDto,
                                            final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void submissionPriceProposal(@Valid @RequestBody final ValueDto valueDto,
                                        final BindingResult bindingResult) {
        Optional.of(bindingResult.hasErrors()).ifPresent(b -> new ValidationException(bindingResult));
    }
}

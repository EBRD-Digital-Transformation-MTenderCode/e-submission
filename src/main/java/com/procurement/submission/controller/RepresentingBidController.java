package com.procurement.submission.controller;

import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.request.DocumentDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.request.ValueDto;
import com.procurement.submission.model.dto.response.QualificationOfferResponseDto;
import com.procurement.submission.service.BidService;
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

    private BidService bidService;

    public RepresentingBidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "qualificationOffer")
    @ResponseStatus(HttpStatus.CREATED)
    public void submissionQualificationProposal(
        @Valid @RequestBody final QualificationOfferDto dataDto,
        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        bidService.insertData(dataDto);
    }

    @PostMapping(value = "/technicalProposal")
    @ResponseStatus(HttpStatus.CREATED)
    public void submissionTechnicalProposal(@Valid @RequestBody final DocumentDto documentDto,
                                            final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }

    @PostMapping(value = "/priceOffer")
    @ResponseStatus(HttpStatus.CREATED)
    public void submissionPriceProposal(@Valid @RequestBody final ValueDto valueDto,
                                        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }
}

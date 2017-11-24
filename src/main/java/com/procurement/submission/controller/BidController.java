package com.procurement.submission.controller;

import com.procurement.submission.exception.ValidationException;
import com.procurement.submission.model.dto.request.BidsGetDto;
import com.procurement.submission.model.dto.request.DocumentDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.request.ValueDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.service.BidService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/submission")
public class BidController {

    private BidService bidService;

    public BidController(final BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "/qualificationOffer")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveQualificationProposal(
        @Valid @RequestBody final QualificationOfferDto dataDto,
        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        bidService.insertData(dataDto);
    }

    @PostMapping(value = "/technicalProposal")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveTechnicalProposal(@Valid @RequestBody final DocumentDto documentDto,
                                      final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }

    @PostMapping(value = "/priceOffer")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePriceProposal(@Valid @RequestBody final ValueDto valueDto,
                                  final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
    }

    @GetMapping(value = "/bibs")
    @ResponseStatus(HttpStatus.OK)
    public List<BidResponse> getBids(@RequestParam final String ocid,
                                     @RequestParam final String procurementMethodDetail,
                                     @RequestParam final String stage,
                                     @RequestParam final String country) {
        final BidsGetDto bidsGetDto = new BidsGetDto(ocid, procurementMethodDetail, stage, country);
        return bidService.getBids(bidsGetDto);
    }
}

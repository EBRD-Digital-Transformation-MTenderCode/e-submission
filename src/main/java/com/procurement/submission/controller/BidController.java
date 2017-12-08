package com.procurement.submission.controller;

import com.procurement.submission.model.dto.request.BidAqpDto;
import com.procurement.submission.model.dto.request.BidsParamDto;
import com.procurement.submission.model.dto.request.DocumentDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.request.ValueDto;
import com.procurement.submission.model.dto.response.Bids;
import com.procurement.submission.service.BidService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Validated
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
        @Valid @RequestBody final QualificationOfferDto dataDto) {
        bidService.insertData(dataDto);
    }

    @PostMapping(value = "/technicalProposal")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveTechnicalProposal(@Valid @RequestBody final DocumentDto documentDto) {
    }

    @PostMapping(value = "/priceOffer")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePriceProposal(@Valid @RequestBody final ValueDto valueDto) {
    }

    @GetMapping(value = "/bids")
    public ResponseEntity<Bids> getBids(@Size(min = 21, max = 21) @RequestParam final String ocid,
                                        @NotBlank @RequestParam final String procurementMethodDetail,
                                        @NotBlank @RequestParam final String stage,
                                        @Size(min = 2, max = 2) @Pattern(regexp = "[a-zA-Z]*")
                                        @RequestParam final String country
    ) {
        final BidsParamDto bidsParamDto = new BidsParamDto(ocid, procurementMethodDetail, stage, country);
        final Bids bids = bidService.getBids(bidsParamDto);
        return new ResponseEntity<>(bids, OK);
    }

    @PatchMapping(value = "/bids")
    @ResponseStatus(OK)
    public void patchBids(@Size(min = 21, max = 21) @RequestParam final String ocid,
                          @NotBlank @RequestParam final String stage,
                          @Valid @NotEmpty @RequestBody final List<BidAqpDto> bidAqpDtos) {
        bidService.patchBids(ocid, stage, bidAqpDtos);
    }
}

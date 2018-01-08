package com.procurement.submission.controller;

import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.request.BidsSelectionDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.dto.response.BidResponseEntity;
import com.procurement.submission.model.dto.response.BidsCopyResponse;
import com.procurement.submission.model.dto.response.BidsResponseEntity;
import com.procurement.submission.service.BidService;
import java.util.ArrayList;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping(path = "/submission")
public class BidController {
    private BidService bidService;

    public BidController(final BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "/createBid")
    @ResponseStatus(CREATED)
    public BidResponseEntity createBid(@Valid @RequestBody final BidRequestDto bidRequest) {
        final BidResponse bidResponse = bidService.createBid(bidRequest);
        return new BidResponseEntity(true, new ArrayList<>(), bidResponse);
    }

    @PostMapping(value = "/updateBid")
    @ResponseStatus(OK)
    public BidResponseEntity updateBid(@Valid @RequestBody final BidRequestDto bidRequest) {
        final BidResponse bidResponse = bidService.updateBid(bidRequest);
        return new BidResponseEntity(true, new ArrayList<>(), bidResponse);
    }

    @PostMapping(value = "/copyBids")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidsCopyResponse> copyBids(@Valid @RequestBody final BidsCopyDto bidsCopyDto) {
        final BidsCopyResponse bids = bidService.copyBids(bidsCopyDto);
        return new BidsResponseEntity<>(true, new ArrayList<>(), bids);
    }

    @GetMapping(value = "/selectionOfBidsForQualificationEvaluation")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidsSelectionDto> selectionBids(
        @Valid @RequestBody final BidsSelectionDto bidsSelectionDto) {
        final BidsSelectionDto bids = bidService.selectionBids(bidsSelectionDto);
        return new BidsResponseEntity<>(true, new ArrayList<>(), bids);
    }
}

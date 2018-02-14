package com.procurement.submission.controller;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.service.BidService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(path = "/submission")
public class BidController {

    private BidService bidService;

    public BidController(final BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "/bid")
    public ResponseEntity<ResponseDto> createBid(@RequestParam final String cpId,
                                                 @RequestParam final String stage,
                                                 @RequestParam final String owner,
                                                 @Valid @RequestBody final BidRequestDto bidDto) {
        return new ResponseEntity<>(bidService.createBid(cpId, stage, owner, bidDto.getBid()), HttpStatus.CREATED);
    }

    @PutMapping(value = "/bid")
    public ResponseEntity<ResponseDto> updateBid(@RequestParam final String cpId,
                                                 @RequestParam final String stage,
                                                 @RequestParam final String token,
                                                 @RequestParam final String owner,
                                                 @Valid @RequestBody final BidRequestDto bidDto) {
        return new ResponseEntity<>(bidService.updateBid(cpId, stage, token, owner, bidDto.getBid()), HttpStatus.OK);
    }

    @PostMapping(value = "/copyBids")
    public ResponseEntity<ResponseDto> copyBids(@RequestParam final String cpId,
                                                @RequestParam final String stage,
                                                @RequestParam final String previousStage,
                                                @Valid @RequestBody final LotsDto lots) {
        return new ResponseEntity<>(bidService.copyBids(cpId, stage, previousStage, lots), HttpStatus.OK);
    }

    @GetMapping(value = "/bids")
    public ResponseEntity<ResponseDto> getBids(@RequestParam final String cpId,
                                               @RequestParam final String stage,
                                               @RequestParam final String country,
                                               @RequestParam final String pmd,
                                               @RequestParam final String status) {
        return new ResponseEntity<>(
                bidService.getBids(cpId, stage, country, pmd, Bid.Status.fromValue(status)),
                HttpStatus.OK);
    }

    @PostMapping(value = "/updateStatus")
    public ResponseEntity<ResponseDto> updateStatus(@RequestParam final String cpId,
                                                    @RequestParam final String stage,
                                                    @RequestParam final String country,
                                                    @RequestParam final String pmd,
                                                    @RequestBody final LotsDto lots) {
        return new ResponseEntity<>(bidService.updateBidsByLots(cpId, stage, country, pmd, lots), HttpStatus.OK);
    }

    @PostMapping(value = "/updateStatusDetail")
    public ResponseEntity<ResponseDto> updateStatusDetail(@RequestParam final String cpId,
                                                          @RequestParam final String stage,
                                                          @RequestParam final String bidId,
                                                          @RequestParam final String awardStatus) {
        return new ResponseEntity<>(bidService.updateStatusDetail(cpId, stage, bidId, awardStatus), HttpStatus.OK);
    }

    @PostMapping(value = "/setFinalStatuses")
    public ResponseEntity<ResponseDto> setFinalStatuses(@RequestParam final String cpId,
                                                        @RequestParam final String stage) {
        return new ResponseEntity<>(bidService.setFinalStatuses(cpId, stage), HttpStatus.OK);
    }
}

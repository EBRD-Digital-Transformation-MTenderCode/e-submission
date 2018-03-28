package com.procurement.submission.controller;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.AwardStatusDetails;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto;
import com.procurement.submission.service.BidService;
import java.time.LocalDateTime;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<ResponseDto> copyBids(@RequestParam(value = "cpid") final String cpId,
                                                @RequestParam(value = "stage") final String newStage,
                                                @RequestParam(value = "previousStage") final String previousStage,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam(value = "startDate") final LocalDateTime startDate,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam(value = "endDate") final LocalDateTime endDate,
                                                @Valid @RequestBody final LotsDto lotsDto) {
        return new ResponseEntity<>(bidService.copyBids(cpId, newStage, previousStage, startDate, endDate, lotsDto),
                HttpStatus.OK);
    }

    @GetMapping(value = "/bids")
    public ResponseEntity<ResponseDto> getPendingBids(@RequestParam final String cpId,
                                                      @RequestParam final String stage,
                                                      @RequestParam final String country,
                                                      @RequestParam final String pmd) {
        return new ResponseEntity<>(bidService.getPendingBids(cpId, stage, country, pmd), HttpStatus.OK);
    }

    @PostMapping(value = "/updateStatus")
    public ResponseEntity<ResponseDto> updateStatus(@RequestParam final String cpId,
                                                    @RequestParam final String stage,
                                                    @RequestParam final String country,
                                                    @RequestParam final String pmd,
                                                    @RequestBody final UnsuccessfulLotsDto unsuccessfulLots) {
        return new ResponseEntity<>(bidService.updateBidsByLots(cpId, stage, country, pmd, unsuccessfulLots),
                HttpStatus.OK);
    }

    @PostMapping(value = "/updateStatusDetails")
    public ResponseEntity<ResponseDto> updateStatusDetails(@RequestParam final String cpId,
                                                           @RequestParam final String stage,
                                                           @RequestParam final String bidId,
                                                           @RequestParam final String awardStatusDetails) {
        return new ResponseEntity<>(bidService.updateStatusDetails(cpId, stage, bidId, AwardStatusDetails.fromValue
                (awardStatusDetails)),
                HttpStatus.OK);
    }

    @PostMapping(value = "/setFinalStatuses")
    public ResponseEntity<ResponseDto> setFinalStatuses(@RequestParam final String cpId,
                                                        @RequestParam final String stage) {
        return new ResponseEntity<>(bidService.setFinalStatuses(cpId, stage), HttpStatus.OK);
    }
}

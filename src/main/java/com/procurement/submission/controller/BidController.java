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
    public ResponseEntity<ResponseDto> createBid(@RequestParam("identifier") final String cpId,
                                                 @RequestParam("stage") final String stage,
                                                 @RequestParam("owner") final String owner,
                                                 @Valid @RequestBody final BidRequestDto data) {
        return new ResponseEntity<>(
                bidService.createBid(cpId, stage, owner, data.getBid()),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/bid")
    public ResponseEntity<ResponseDto> updateBid(@RequestParam("identifier") final String cpId,
                                                 @RequestParam("stage") final String stage,
                                                 @RequestParam("token") final String token,
                                                 @RequestParam("owner") final String owner,
                                                 @Valid @RequestBody final BidRequestDto data) {
        return new ResponseEntity<>(
                bidService.updateBid(cpId, stage, token, owner, data.getBid()),
                HttpStatus.OK);
    }

    @PostMapping(value = "/copyBids")
    public ResponseEntity<ResponseDto> copyBids(@RequestParam("identifier") final String cpId,
                                                @RequestParam("stage") final String newStage,
                                                @RequestParam("previousStage") final String previousStage,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam("startDate") final LocalDateTime startDate,
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                @RequestParam("endDate") final LocalDateTime endDate,
                                                @Valid @RequestBody final LotsDto data) {
        return new ResponseEntity<>(
                bidService.copyBids(cpId, newStage, previousStage, startDate, endDate, data),
                HttpStatus.OK);
    }

    @GetMapping(value = "/bids")
    public ResponseEntity<ResponseDto> getPendingBids(@RequestParam("identifier") final String cpId,
                                                      @RequestParam("stage") final String stage,
                                                      @RequestParam("country") final String country,
                                                      @RequestParam("pmd") final String pmd) {
        return new ResponseEntity<>(
                bidService.getPendingBids(cpId, stage, country, pmd),
                HttpStatus.OK);
    }

    @PostMapping(value = "/updateStatus")
    public ResponseEntity<ResponseDto> updateStatus(@RequestParam("identifier") final String cpId,
                                                    @RequestParam("stage") final String stage,
                                                    @RequestParam("country") final String country,
                                                    @RequestParam("pmd") final String pmd,
                                                    @RequestBody final UnsuccessfulLotsDto data) {
        return new ResponseEntity<>(
                bidService.updateBidsByLots(cpId, stage, country, pmd, data),
                HttpStatus.OK);
    }

    @PostMapping(value = "/updateStatusDetails")
    public ResponseEntity<ResponseDto> updateStatusDetails(@RequestParam("identifier") final String cpId,
                                                           @RequestParam("stage") final String stage,
                                                           @RequestParam("bidId") final String bidId,
                                                           @RequestParam("awardStatusDetails") final String awardStatusDetails) {
        return new ResponseEntity<>(
                bidService.updateStatusDetails(cpId, stage, bidId, AwardStatusDetails.fromValue(awardStatusDetails)),
                HttpStatus.OK);
    }

    @PostMapping(value = "/setFinalStatuses")
    public ResponseEntity<ResponseDto> setFinalStatuses(@RequestParam("identifier") final String cpId,
                                                        @RequestParam("stage") final String stage) {
        return new ResponseEntity<>(
                bidService.setFinalStatuses(cpId, stage),
                HttpStatus.OK);
    }
}

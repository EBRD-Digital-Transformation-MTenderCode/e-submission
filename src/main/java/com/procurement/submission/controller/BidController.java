package com.procurement.submission.controller;

import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.service.BidService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping(path = "/submission")
public class BidController {

    private BidService bidService;

    public BidController(final BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "/bid")
    public ResponseEntity<ResponseDto> createBid(@RequestParam("ocid") final String ocId,
                                                 @RequestParam("stage") final String stage,
                                                 @RequestParam("owner") final String owner,
                                                 @Valid @RequestBody final Bid bidDto) {
        return new ResponseEntity<>(bidService.createBid(ocId, stage, owner, bidDto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/bid")
    public ResponseEntity<ResponseDto> updateBid(@RequestParam("ocid") final String ocId,
                                                 @RequestParam("stage") final String stage,
                                                 @RequestParam("token") final String token,
                                                 @RequestParam("owner") final String owner,
                                                 @Valid @RequestBody final Bid bidDto) {
        return new ResponseEntity<>(bidService.updateBid(ocId, stage, token, owner, bidDto), HttpStatus.OK);
    }

    @PostMapping(value = "/copyBids")
    public ResponseEntity<ResponseDto> copyBids(@RequestParam("ocid") final String ocId,
                                                @RequestParam("stage") final String stage,
                                                @RequestParam("stage") final String previousStage,
                                                @Valid @RequestBody final BidsCopyDto bidsCopyDto) {
        return new ResponseEntity<>(bidService.copyBids(ocId, stage, previousStage, bidsCopyDto), HttpStatus.OK);
    }


    @GetMapping(value = "/bids")
    public ResponseEntity<ResponseDto> getBids(@RequestParam final String ocId,
                                               @RequestParam final String country,
                                               @RequestParam final String pmd,
                                               @RequestParam final String stage,
                                               @RequestParam final Bid.Status status) {
        return new ResponseEntity<>(bidService.getBids(ocId, country, pmd, stage, status), HttpStatus.OK);
    }

    @PutMapping(value = "/updateStatus")
    @ResponseStatus(OK)
    public ResponseEntity<ResponseDto> updateStatus(@RequestParam final String ocId,
                                                    @RequestParam final String stage,
                                                    @RequestParam final String country,
                                                    @RequestParam final String pmd,
                                                    @RequestBody final LotsDto lots) {
        return new ResponseEntity<>(bidService.updateBidsByLots(ocId, stage, country, pmd, lots), HttpStatus.OK);
    }

    @PutMapping(value = "/updateStatusDetail")
    @ResponseStatus(OK)
    public ResponseEntity<ResponseDto> updateStatusDetail(@RequestParam final String ocId,
                                                          @RequestParam final String stage,
                                                          @RequestParam final String bidId,
                                                          @RequestParam final String awardStatus) {
        return new ResponseEntity<>(bidService.updateStatusDetail(ocId, stage, bidId, awardStatus), HttpStatus.OK);
    }

    @PutMapping(value = "/setFinalStatuses")
    @ResponseStatus(OK)
    public ResponseEntity<ResponseDto> setFinalStatuses(@RequestParam final String ocId,
                                                        @RequestParam final String stage) {
        return new ResponseEntity<>(bidService.setFinalStatuses(ocId, stage), HttpStatus.OK);
    }
}

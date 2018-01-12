package com.procurement.submission.controller;

import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.request.BidsSelectionDto;
import com.procurement.submission.model.dto.request.BidsUpdateByLotsDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.dto.response.BidResponseEntity;
import com.procurement.submission.model.dto.response.BidWithdrawnRs;
import com.procurement.submission.model.dto.response.BidsCopyResponse;
import com.procurement.submission.model.dto.response.BidsResponseEntity;
import com.procurement.submission.model.dto.response.BidsSelectionResponse;
import com.procurement.submission.model.dto.response.BidsWithdrawnRs;
import com.procurement.submission.model.dto.response.CommonBidResponse;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.service.BidService;
import java.util.ArrayList;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping(path = "/submission")
public class BidController {
    private static final int OCID_LENGTH = 21;
    private BidService bidService;

    public BidController(final BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "/createBid")
    @ResponseStatus(CREATED)
    public BidResponseEntity createBid(@Valid @RequestBody final BidRequestDto bidRequest) {
        final CommonBidResponse commonBidResponse = bidService.createBid(bidRequest);
        return new BidResponseEntity(true, new ArrayList<>(), commonBidResponse);
    }

    @PostMapping(value = "/updateBid")
    @ResponseStatus(OK)
    public BidResponseEntity updateBid(@Valid @RequestBody final BidRequestDto bidRequest) {
        final CommonBidResponse commonBidResponse = bidService.updateBid(bidRequest);
        return new BidResponseEntity(true, new ArrayList<>(), commonBidResponse);
    }

    @PostMapping(value = "/copyBids")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidsCopyResponse> copyBids(@Valid @RequestBody final BidsCopyDto bidsCopyDto) {
        final BidsCopyResponse bids = bidService.copyBids(bidsCopyDto);
        return new BidsResponseEntity<>(true, new ArrayList<>(), bids);
    }

    /**
     * @param cpid
     * @param country
     * @param pmd     - procurementMethodDetails
     * @param stage
     * @param status  - must write in uppercase
     * @return
     * @apiNote .../submission/selectionOfBids/ocds-213czf-000-00001?country=UA&pmd=method&stage=stage&status=PENDING
     */
    @GetMapping(value = "/selectionOfBids/{cpid}")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidsSelectionResponse> selectionOfBids(
        @PathVariable @Size(min = OCID_LENGTH, max = OCID_LENGTH) final String cpid,
        @RequestParam @Size(min = 2, max = 2) @Pattern(regexp = "[a-zA-Z]*") final String country,
        @RequestParam @NotBlank final String pmd,
        @RequestParam @NotBlank final String stage,
        @RequestParam final Bid.Status status) {
        final BidsSelectionResponse bids =
            bidService.selectionBids(new BidsSelectionDto(cpid, country, pmd, stage, status));
        return new BidsResponseEntity<>(true, new ArrayList<>(), bids);
    }

    @PostMapping(value = "/updateStatus/{cpid}")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidsWithdrawnRs> updateStatus(
        @PathVariable @Size(min = OCID_LENGTH, max = OCID_LENGTH) final String cpid,
        @RequestParam @NotBlank final String stage,
        @RequestParam @Size(min = 2, max = 2) @Pattern(regexp = "[a-zA-Z]*") final String country,
        @RequestParam @NotBlank final String pmd,
        @RequestBody @NotNull final LotsDto lots) {
        final BidsWithdrawnRs bidsWithdrawnRs =
            bidService.updateBidsByLots(new BidsUpdateByLotsDto(cpid, stage, country, pmd, lots));
        return new BidsResponseEntity<>(true, new ArrayList<>(), bidsWithdrawnRs);
    }

    @PostMapping(value = "/updateStatusDetail/{cpid}")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidWithdrawnRs> updateStatusDetail(
        @PathVariable @Size(min = OCID_LENGTH, max = OCID_LENGTH) final String cpid,
        @RequestParam @NotBlank final String stage,
        @RequestParam @NotBlank final String bidId,
        @RequestParam @NotBlank final String awardStatus
    ) {
        final BidWithdrawnRs bidWithdrawnRs = bidService.updateStatusDetail(cpid, stage, bidId, awardStatus);
        return new BidsResponseEntity<>(true, new ArrayList<>(), bidWithdrawnRs);
    }

    @PostMapping(value = "/setFinalStatuses/{cpid}")
    @ResponseStatus(OK)
    public BidsResponseEntity<BidWithdrawnRs> setFinalStatuses(
        @PathVariable @Size(min = OCID_LENGTH, max = OCID_LENGTH) final String cpid,
        @RequestParam @NotBlank final String stage
    ) {
        final BidWithdrawnRs bidWithdrawnRs = bidService.setFinalStatuses(cpid, stage);
        return new BidsResponseEntity<>(true, new ArrayList<>(), bidWithdrawnRs);
    }
}

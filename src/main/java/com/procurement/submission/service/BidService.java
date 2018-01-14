package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.request.BidsSelectionDto;
import com.procurement.submission.model.dto.request.BidsUpdateByLotsDto;
import com.procurement.submission.model.dto.response.BidWithdrawnRs;
import com.procurement.submission.model.dto.response.BidsCopyResponse;
import com.procurement.submission.model.dto.response.BidsSelectionResponse;
import com.procurement.submission.model.dto.response.BidsWithdrawnRs;
import com.procurement.submission.model.dto.response.CommonBidResponse;
import java.util.List;

public interface BidService {

    CommonBidResponse createBid(BidRequestDto bidRequest);

    CommonBidResponse updateBid(BidRequestDto bidRequest);

    BidsCopyResponse copyBids(BidsCopyDto bidsCopyDto);

    BidsSelectionResponse selectionBids(BidsSelectionDto bidsSelectionDto);

    BidsWithdrawnRs updateBidsByLots(BidsUpdateByLotsDto bidsUpdateByLotsDto);

    BidWithdrawnRs updateStatusDetail(String cpid, String stage, String bidId, String awardStatus);

    List<BidWithdrawnRs> setFinalStatuses(String cpid, String stage);
}

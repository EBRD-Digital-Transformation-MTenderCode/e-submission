package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.dto.response.BidsCopyResponse;

public interface BidService {

    BidResponse createBid(BidRequestDto bidRequest);

    BidResponse updateBid(BidRequestDto bidRequest);

    BidsCopyResponse copyBids(BidsCopyDto bidsCopyDto);
}

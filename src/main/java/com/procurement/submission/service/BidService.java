package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.response.BidResponse;

public interface BidService {

    BidResponse createBid(BidRequestDto bidRequest);

    BidResponse updateBid(BidRequestDto bidRequest);
}

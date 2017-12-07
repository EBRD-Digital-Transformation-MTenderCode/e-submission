package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidAqpDto;
import com.procurement.submission.model.dto.request.BidsParamDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.Bids;
import java.util.List;

public interface BidService {

    void insertData(QualificationOfferDto dataDto);

    Bids getBids(BidsParamDto bidsParamDto);

    void patchBids(String ocid, String stage, List<BidAqpDto> bidAqpDtos);
}

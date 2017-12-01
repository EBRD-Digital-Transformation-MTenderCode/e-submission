package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidsGetDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.Bids;

public interface BidService {

    void insertData(QualificationOfferDto dataDto);

    Bids getBids(BidsGetDto bidsGetDto);
}

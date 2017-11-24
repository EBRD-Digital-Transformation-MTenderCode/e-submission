package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidsGetDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.BidResponse;
import java.util.List;

public interface BidService {

    void insertData(QualificationOfferDto dataDto);

    List<BidResponse> getBids(BidsGetDto bidsGetDto);
}

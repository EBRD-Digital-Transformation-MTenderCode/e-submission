package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.BidAqpDto;
import com.procurement.submission.model.dto.request.BidsParamDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.BidsResponse;
import java.util.List;

public interface BidService {

    void insertData(QualificationOfferDto dataDto);

    BidsResponse getBids(BidsParamDto bidsParamDto);

    void patchBids(String ocid, String stage, List<BidAqpDto> bidAqpDtos);

    BidsResponse changeBidsStatus(String ocid, String stage, String newStage);
}

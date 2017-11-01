package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.QualificationOfferResponseDto;

public interface BidService {

    QualificationOfferResponseDto insertQualificationOffer(QualificationOfferDto qualificationOfferDto);
}

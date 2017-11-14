package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class BidServiceImpl implements BidService {
    private final PeriodService periodService;
    private final BidRepository bidRepository;
    private final ConversionService conversionService;

    public BidServiceImpl(final PeriodService periodService,
                          final BidRepository bidRepository,
                          final ConversionService conversionService) {
        this.periodService = periodService;
        this.bidRepository = bidRepository;
        this.conversionService = conversionService;
    }

    @Override
    public void insertData(final QualificationOfferDto qualificationOfferDto) {
        periodService.checkPeriod(qualificationOfferDto.getOcid());
        Optional.ofNullable(conversionService.convert(qualificationOfferDto, BidEntity.class))
                .ifPresent(bidRepository::save);
    }
}

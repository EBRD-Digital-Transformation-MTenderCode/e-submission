package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class BidServiceImpl implements BidService {
    private final PeriodService periodService;
    private final BidRepository bidRepository;
    private final ConversionService conversionService;
    private final JsonUtil jsonUtil;

    public BidServiceImpl(final PeriodService periodService,
                          final BidRepository bidRepository,
                          final ConversionService conversionService,
                          final JsonUtil jsonUtil) {
        this.periodService = periodService;
        this.bidRepository = bidRepository;
        this.conversionService = conversionService;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void insertData(final QualificationOfferDto qualificationOfferDto) {
        periodService.checkPeriod(qualificationOfferDto.getOcid());
        getBidEntity(qualificationOfferDto)
            .ifPresent(bidRepository::save);
    }

    private Optional<BidEntity> getBidEntity(final QualificationOfferDto qualificationOfferDto) {
        return Optional.ofNullable(conversionService.convert(qualificationOfferDto, BidEntity.class))
                       .map(e -> {
                           e.setJsonData(jsonUtil.toJson(qualificationOfferDto.getBid()));
                           return e;
                       });
    }
}

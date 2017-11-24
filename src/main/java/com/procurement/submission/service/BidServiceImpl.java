package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.BidsGetDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class BidServiceImpl implements BidService {
    private final PeriodService periodService;
    private final BidRepository bidRepository;
    private final ConversionService conversionService;
    private final JsonUtil jsonUtil;
    private final RulesService rulesService;

    public BidServiceImpl(final PeriodService periodService,
                          final BidRepository bidRepository,
                          final ConversionService conversionService,
                          final JsonUtil jsonUtil,
                          final RulesService rulesService) {
        this.periodService = periodService;
        this.bidRepository = bidRepository;
        this.conversionService = conversionService;
        this.jsonUtil = jsonUtil;
        this.rulesService = rulesService;
    }

    @Override
    public void insertData(final QualificationOfferDto qualificationOfferDto) {
        periodService.checkPeriod(qualificationOfferDto.getOcid());
        getBidEntity(qualificationOfferDto)
            .ifPresent(bidRepository::save);
    }

    @Override
    public List<BidResponse> getBids(final BidsGetDto bidsGetDto) {
        final List<BidEntity> bids = bidRepository.findAllByOcIdAndStage(bidsGetDto.getOcid(), bidsGetDto.getStage());
        final Map<String, Set<BidQualificationDto>> bidQualificationDtos = getBidQualificationDtos(bids);
        final String key = bidQualificationDtos.keySet().iterator().next();
        final Set<BidQualificationDto> bidQualificationDtoList = bidQualificationDtos.get(key);
        final int minBids = getRulesMinBids(bidsGetDto);
// FIXME: 24.11.17 for each bid
        if (bidQualificationDtoList.size() >= minBids) {
            return getBidResponses(bidQualificationDtoList);
        } else throw new ErrorException("Insufficient number of unique bids");
    }

    private Map<String, Set<BidQualificationDto>> getBidQualificationDtos(final List<BidEntity> bids) {
// FIXME: 24.11.17 bidQualificationDto.getRelatedLots().get(0) - find for each lot
        return bids.stream()
                   .map(bidEntity -> jsonUtil.toObject(BidQualificationDto.class, bidEntity.getJsonData()))
                   .collect(groupingBy(bidQualificationDto -> bidQualificationDto.getRelatedLots().get(0), toSet()));
    }

    private int getRulesMinBids(final BidsGetDto bidsGetDto) {
        return rulesService.getRulesMinBids(bidsGetDto.getCountry(), bidsGetDto.getProcurementMethodDetail());
    }

    private List<BidResponse> getBidResponses(Set<BidQualificationDto> bidQualificationDtoList) {
        return bidQualificationDtoList.stream()
                                      .map(b -> conversionService.convert(b, BidResponse.class))
                                      .collect(toList());
    }

    private Optional<BidEntity> getBidEntity(final QualificationOfferDto qualificationOfferDto) {
        return Optional.ofNullable(conversionService.convert(qualificationOfferDto, BidEntity.class))
                       .map(e -> {
                           e.setJsonData(jsonUtil.toJson(qualificationOfferDto.getBid()));
                           return e;
                       });
    }
}

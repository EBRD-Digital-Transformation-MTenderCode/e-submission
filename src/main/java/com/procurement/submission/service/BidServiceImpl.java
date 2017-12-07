package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.*;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.dto.response.Bids;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;

import java.util.*;
import java.util.function.Function;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.*;

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
    public Bids getBids(final BidsParamDto bidsParamDto) {
        final List<BidEntity> bids = bidRepository.findAllByOcIdAndStage(
                bidsParamDto.getOcid(), bidsParamDto.getStage());
        final Map<String, Set<BidQualificationDto>> bidQualificationDtos = getBidQualificationDtos(bids);
        final String key = bidQualificationDtos.keySet().iterator().next();
        final Set<BidQualificationDto> bidQualificationDtoList = bidQualificationDtos.get(key);
        final int minBids = getRulesMinBids(bidsParamDto);
// FIXME: 24.11.17 for each bid
        if (bidQualificationDtoList.size() >= minBids) {
            return new Bids(getBidResponses(bidQualificationDtoList));
        } else {
            throw new ErrorException("Insufficient number of unique bids");
        }
    }

    @Override
    public void patchBids(final String ocid,
                          final String stage,
                          final List<BidAqpDto> bidAqpDtos) {
        final Set<UUID> uuids = bidAqpDtos.stream()
                .map(b -> UUID.fromString(b.getId()))
                .collect(toSet());
        final List<BidEntity> bidEntities = bidRepository.findAllByOcIdAndStageAndBidId(ocid, stage, uuids);
        final Map<String, BidAqpDto> idIdAqpDtoMap = bidAqpDtos.stream()
                .collect(toMap(BidAqpDto::getId, Function.identity()));
        final List<BidEntity> bidEntitiesAfterModify = bidEntities.stream()
                .map(bidEntity -> setDataToBidEntity(bidEntity, idIdAqpDtoMap.get(bidEntity.getBidId().toString())))
                .collect(toList());
        bidRepository.saveAll(bidEntitiesAfterModify);
    }

    private Map<String, Set<BidQualificationDto>> getBidQualificationDtos(final List<BidEntity> bids) {
// FIXME: 24.11.17 bidQualificationDto.getRelatedLots().get(0) - find for each lot
        return bids.stream()
                   .map(bidEntity -> jsonUtil.toObject(BidQualificationDto.class, bidEntity.getJsonData()))
                   .collect(groupingBy(bidQualificationDto -> bidQualificationDto.getRelatedLots().get(0), toSet()));
    }

    private int getRulesMinBids(final BidsParamDto bidsParamDto) {
        return rulesService.getRulesMinBids(bidsParamDto.getCountry(), bidsParamDto.getProcurementMethodDetail());
    }

    private List<BidResponse> getBidResponses(final Set<BidQualificationDto> bidQualificationDtoList) {
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

    private BidEntity setDataToBidEntity(final BidEntity bidEntity, final BidAqpDto bidAqpDto) {
        final BidQualificationDto bidQualificationDto =
                jsonUtil.toObject(BidQualificationDto.class, bidEntity.getJsonData());
        final List<DocumentDto> documents = bidQualificationDto.getDocuments();
        documents.addAll(bidAqpDto.getDocuments());
        bidEntity.setJsonData(jsonUtil.toJson(bidQualificationDto));
        bidEntity.setStatus(bidAqpDto.getStatus());
        return bidEntity;
    }
}

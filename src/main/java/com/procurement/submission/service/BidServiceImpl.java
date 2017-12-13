package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.BidAqpDto;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.BidStatus;
import com.procurement.submission.model.dto.request.BidsParamDto;
import com.procurement.submission.model.dto.request.DocumentDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.dto.response.BidsResponse;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
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
    public BidsResponse getBids(final BidsParamDto bidsParamDto) {
        final List<BidEntity> bids = bidRepository.findAllByOcIdAndStage(
            bidsParamDto.getOcid(), bidsParamDto.getStage());
        final Map<String, Set<BidQualificationDto>> bidQualificationDtos = getBidQualificationDtos(bids);
        final String key = bidQualificationDtos.keySet().iterator().next();
        final Set<BidQualificationDto> bidQualificationDtoList = bidQualificationDtos.get(key);
        final int minBids = getRulesMinBids(bidsParamDto);
// FIXME: 24.11.17 for each bid
        if (bidQualificationDtoList.size() >= minBids) {
            return new BidsResponse(getBidResponses(bidQualificationDtoList));
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
                                                                  .map(bidEntity -> setDataToBidEntity(bidEntity,
                                                                      idIdAqpDtoMap
                                                                          .get(bidEntity.getBidId().toString())))
                                                                  .collect(toList());
        bidRepository.saveAll(bidEntitiesAfterModify);
    }

    @Override
    public BidsResponse changeBidsStatus(final String ocid, final String oldStage,
                                         final String newStage) {
        final List<BidEntity> filteredBidEntities =
            Optional.ofNullable(bidRepository.findAllByOcIdAndStage(ocid, oldStage).stream()
                                             .filter(b -> b.getStatus()
                                                           .equals(BidStatus.VALID))
                                             .collect(toList()))
                    .orElseThrow(() -> new ErrorException("We don't have valid data with ocid=" + ocid +
                        " and previous stage=" + oldStage));
        final List<BidEntity> newBibs =
            filteredBidEntities.stream()
                               .map(bidEntity -> createNewBid(bidEntity, newStage))
                               .collect(toList());
        bidRepository.saveAll(newBibs);
        return createBidsResponse(newBibs);
    }

    private BidEntity createNewBid(final BidEntity oldBid, final String newStage) {
        final BidEntity newBid = new BidEntity();
        newBid.setOcId(oldBid.getOcId());
        newBid.setStage(newStage);
        newBid.setBidId(oldBid.getBidId());
        final String newJsonData = createNewJsonData(oldBid.getJsonData());
        newBid.setJsonData(newJsonData);
        newBid.setStatus(BidStatus.INVITED);
        return newBid;
    }

    private String createNewJsonData(final String jsonData) {
        final BidQualificationDto oldBqd = jsonUtil.toObject(BidQualificationDto.class, jsonData);
        final BidQualificationDto newBqd = new BidQualificationDto(
            oldBqd.getId(), LocalDateTime.now(), BidStatus.INVITED, oldBqd.getTenderers(),
            new ArrayList<>(), oldBqd.getRelatedLots());
        return jsonUtil.toJson(newBqd);
    }

    private BidsResponse createBidsResponse(final List<BidEntity> newBibs) {
        return new BidsResponse(
            newBibs.stream()
                   .map(bidEntity -> jsonUtil.toObject(BidQualificationDto.class, bidEntity.getJsonData()))
                   .map(this::convertBidQualificationDtoToBidResponse)
                   .collect(toList())
        );
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
                                      .map(this::convertBidQualificationDtoToBidResponse)
                                      .collect(toList());
    }

    private BidResponse convertBidQualificationDtoToBidResponse(final BidQualificationDto b) {
        return conversionService.convert(b, BidResponse.class);
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

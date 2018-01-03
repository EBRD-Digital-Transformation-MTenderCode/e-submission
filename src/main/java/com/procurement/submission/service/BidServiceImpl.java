package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.dto.response.BidsCopyResponse;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    public BidResponse createBid(final BidRequestDto bidRequest) {
        periodService.checkPeriod(bidRequest.getOcid());
        final List<BidEntity> bidEntities =
            bidRepository.findAllByOcIdAndStage(bidRequest.getOcid(), bidRequest.getStage());
        if (!bidEntities.isEmpty()) {
            checkTenderers(bidEntities, bidRequest);
        }
        final BidEntity bid = createNewBidEntity(bidRequest);
        bidRepository.save(bid);
        return createBidResponse(bid);
    }

    @Override
    public BidResponse updateBid(final BidRequestDto bidRequest) {
        periodService.checkPeriod(bidRequest.getOcid());
        final BidEntity bidEntity = updateBidEntity(bidRequest);
        return createBidResponse(bidEntity);
    }

    @Override
    public BidsCopyResponse copyBids(final BidsCopyDto bidsCopyDto) {
        final List<BidEntity> bidEntities =
            bidRepository.findAllByOcIdAndStage(bidsCopyDto.getOcId(), bidsCopyDto.getPreviousStage());
        final Map<BidEntity, Bid> entityBidMap = filteringValid(bidEntities);
        final Map<BidEntity, Bid> newBidsMap = createBidCopy(bidsCopyDto, entityBidMap);
        bidRepository.saveAll(newBidsMap.keySet());
        final BidsCopyResponse.Bids bids = new BidsCopyResponse.Bids(new ArrayList<>(newBidsMap.values()));
        return new BidsCopyResponse(bidsCopyDto.getOcId(), bids);
    }

    private Map<BidEntity, Bid> createBidCopy(final BidsCopyDto bidsCopyDto,
                                              final Map<BidEntity, Bid> entityBidMap) {
        final List<String> lotsDto = collectLots(bidsCopyDto);
        return entityBidMap.entrySet().stream()
                           .filter(e -> containsAny(e.getValue().getRelatedLots(), lotsDto))
                           .map(e -> copyBidEntity(e, bidsCopyDto.getStage()))
                           .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private HashMap<BidEntity, Bid> filteringValid(final List<BidEntity> bidEntities) {
        return bidEntities.stream()
                          .filter(b -> b.getStatus() == Bid.Status.VALID)
                          .collect(
                              HashMap<BidEntity, Bid>::new,
                              (k, v) -> k.put(v, jsonUtil.toObject(Bid.class, v.getJsonData())),
                              HashMap::putAll);
    }

    private void checkTenderers(final List<BidEntity> bidEntities, final BidRequestDto bidRequest) {
        final List<Bid> bids = getCollectedBids(bidEntities);
        if (isExistTenderers(bids, bidRequest)) {
            throw new ErrorException("We have Bid with this Lots and Tenderers");
        }
    }

    private boolean isExistTenderers(final List<Bid> bids, final BidRequestDto bidRequest) {
        final List<String> bidRequestRelatedLots = bidRequest.getBid().getRelatedLots();
        final BiPredicate<List<OrganizationReference>, List<OrganizationReference>> predicate =
            getTenderersBiPredicate();
        final List<OrganizationReference> bidRequestTenderers = bidRequest.getBid().getTenderers();
        return bids.stream()
                   .filter(b -> b.getRelatedLots().containsAll(bidRequestRelatedLots))
                   .anyMatch(b -> predicate.test(b.getTenderers(), bidRequestTenderers));
    }

    private List<Bid> getCollectedBids(final List<BidEntity> bidEntities) {
        return bidEntities.stream()
                          .map(b -> jsonUtil.toObject(Bid.class, b.getJsonData()))
                          .collect(toList());
    }

    private BiPredicate<List<OrganizationReference>, List<OrganizationReference>> getTenderersBiPredicate() {
        return (tenderersFromDb, tenderersFromRequest) -> {
            if (tenderersFromDb.size() == tenderersFromRequest.size() &&
                tenderersFromDb.containsAll(tenderersFromRequest)) {
                return true;
            }
            return false;
        };
    }

    private Map.Entry<BidEntity, Bid> copyBidEntity(final Map.Entry<BidEntity, Bid> entrySet, final String newStage) {
        final BidEntity oldBidEntity = entrySet.getKey();
        final BidEntity newBidEntity = new BidEntity();
        newBidEntity.setOcId(oldBidEntity.getOcId());
        newBidEntity.setStage(newStage);
        newBidEntity.setBidId(oldBidEntity.getBidId());
        newBidEntity.setBidToken(oldBidEntity.getBidToken());
        Bid.Status newStatus = Bid.Status.INVITED;
        newBidEntity.setStatus(newStatus);
        final LocalDateTime dateTimeNow = LocalDateTime.now();
        newBidEntity.setCreatedDate(dateTimeNow);
        newBidEntity.setPendingDate(null);
        final Bid oldBid = entrySet.getValue();
        final Bid newBid = new Bid(oldBid.getId(), dateTimeNow, newStatus, oldBid.getTenderers(), null, null,
            oldBid.getRelatedLots());
        newBidEntity.setJsonData(jsonUtil.toJson(newBid));
        return new AbstractMap.SimpleEntry<>(newBidEntity, newBid);
    }

    private BidEntity createNewBidEntity(final BidRequestDto requestDto) {
        final LocalDateTime dateTimeNow = LocalDateTime.now();
        requestDto.getBid().setDate(dateTimeNow);
        requestDto.getBid().setId(UUIDs.timeBased().toString());
        requestDto.getBid().setStatus(Bid.Status.PENDING);
        final BidEntity bidEntity = conversionService.convert(requestDto, BidEntity.class);
        bidEntity.setStatus(Bid.Status.PENDING);
        bidEntity.setPendingDate(dateTimeNow);
        bidEntity.setCreatedDate(dateTimeNow);
        bidEntity.setJsonData(jsonUtil.toJson(requestDto.getBid()));
        return bidEntity;
    }

    private BidEntity updateBidEntity(final BidRequestDto requestDto) {
        final BidEntity convertedBidEntity = conversionService.convert(requestDto, BidEntity.class);
        final BidEntity oldBidEntity = bidRepository.findByOcIdAndStageAndBidIdAndBidToken(
            requestDto.getOcid(), requestDto.getStage(), UUID.fromString(requestDto.getBid().getId()),
            UUID.fromString(requestDto.getBidToken()));
        if (oldBidEntity != null) {
            requestDto.getBid().setDate(LocalDateTime.now());
            convertedBidEntity.setJsonData(jsonUtil.toJson(requestDto));
            convertedBidEntity.setCreatedDate(oldBidEntity.getCreatedDate());
            return convertedBidEntity;
        }
        throw new ErrorException("Don't have data.");
    }

    private BidResponse createBidResponse(final BidEntity bid) {
        final BidResponse bidResponse = conversionService.convert(bid, BidResponse.class);
        bidResponse.setBid(jsonUtil.toObject(Bid.class, bid.getJsonData()));
        return bidResponse;
    }

    private List<String> collectLots(final BidsCopyDto bidsCopyDto) {
        return bidsCopyDto.getLots().stream()
                          .map(BidsCopyDto.Lot::getId)
                          .collect(toList());
    }

    private <T> boolean containsAny(Collection<T> src, Collection<T> dest) {
        for (T value : dest) {
            if (src.contains(value)) {
                System.out.println(value);
                return true;
            }
        }
        return false;
    }
}

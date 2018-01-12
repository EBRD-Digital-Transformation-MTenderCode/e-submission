package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.request.BidsCopyDto;
import com.procurement.submission.model.dto.request.BidsSelectionDto;
import com.procurement.submission.model.dto.request.BidsUpdateByLotsDto;
import com.procurement.submission.model.dto.request.LotDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.dto.response.BidWithdrawnRs;
import com.procurement.submission.model.dto.response.BidsCopyResponse;
import com.procurement.submission.model.dto.response.BidsSelectionResponse;
import com.procurement.submission.model.dto.response.BidsWithdrawnRs;
import com.procurement.submission.model.dto.response.CommonBidResponse;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
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
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static com.procurement.submission.model.ocds.Bid.Status.DISQUALIFIED;
import static com.procurement.submission.model.ocds.Bid.Status.INVITED;
import static com.procurement.submission.model.ocds.Bid.Status.PENDING;
import static com.procurement.submission.model.ocds.Bid.Status.VALID;
import static com.procurement.submission.model.ocds.Bid.Status.WITHDRAWN;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
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
    public CommonBidResponse createBid(final BidRequestDto bidRequest) {
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
    public CommonBidResponse updateBid(final BidRequestDto bidRequest) {
        periodService.checkPeriod(bidRequest.getOcid());
        final BidEntity bidEntity = updateBidEntity(bidRequest);
        return createBidResponse(bidEntity);
    }

    @Override
    public BidsCopyResponse copyBids(final BidsCopyDto bidsCopyDto) {
        final List<BidEntity> bidEntities =
            bidRepository.findAllByOcIdAndStage(bidsCopyDto.getOcId(), bidsCopyDto.getPreviousStage());
        if (bidEntities.isEmpty()) {
            throw new ErrorException("Sorry guys, we don't have Bids.");
        }
        final Map<BidEntity, Bid> entityBidMap = filterByStatus(bidEntities, PENDING);
        final Map<BidEntity, Bid> newBidsMap = createBidCopy(bidsCopyDto, entityBidMap);
        bidRepository.saveAll(newBidsMap.keySet());
        final BidsCopyResponse.Bids bids = new BidsCopyResponse.Bids(new ArrayList<>(newBidsMap.values()));
        return new BidsCopyResponse(bidsCopyDto.getOcId(), bids);
    }

    @Override
    public BidsSelectionResponse selectionBids(final BidsSelectionDto bidsSelectionDto) {
        boolean isPeriod = periodService.isPeriod(bidsSelectionDto.getOcId());
        if (isPeriod) {
            throw new ErrorException("Period has not yet expired");
        }
        final List<BidEntity> pendingBids =
            pendingFilter(bidRepository.findAllByOcIdAndStage(bidsSelectionDto.getOcId(), bidsSelectionDto.getStage()));
        int rulesMinBids = rulesService.getRulesMinBids(bidsSelectionDto.getCountry(), bidsSelectionDto.getPmd());
        if (pendingBids.size() < rulesMinBids) {
            throw new ErrorException("Bids with status PENDING are less minimum count of bids.");
        }
        final List<BidsSelectionResponse.Bid> responseBids = pendingBids.stream()
                                                                        .map(this::convertBids)
                                                                        .collect(toList());
        return new BidsSelectionResponse(bidsSelectionDto.getOcId(), responseBids);
    }

    @Override
    public BidsWithdrawnRs updateBidsByLots(final BidsUpdateByLotsDto bidsDto) {
        final List<BidEntity> allDidEntities =
            bidRepository.findAllByOcIdAndStage(bidsDto.getOcid(), bidsDto.getStage());
        if (allDidEntities.isEmpty()) {
            throw new ErrorException("You don't have bids.");
        }

        //collect invited bids
        final Map<BidEntity, Bid> mapInvitedBids = filterByStatus(allDidEntities, INVITED);
        mapInvitedBids.entrySet().forEach(e -> setStatus(e, WITHDRAWN));
        final List<BidEntity> bidEntitiesWithdrawn = collectBids(mapInvitedBids);
        final List<OrganizationReference> organizationReferencesRs = new ArrayList<>();
        final List<BidWithdrawnRs> responseBids = new ArrayList<>();
        mapInvitedBids.forEach((key, value) -> organizationReferencesRs.addAll(value.getTenderers()));
        mapInvitedBids.entrySet().stream()
                      .map(e -> conversionService.convert(e.getValue(), BidWithdrawnRs.class))
                      .forEach(responseBids::add);

        //collect pending bids by lost and with rule
        final Map<BidEntity, Bid> mapPendingBids = filterByStatus(allDidEntities, PENDING);
        final List<String> lotsStr = collectLots(bidsDto.getLots());
        final Map<BidEntity, Bid> mapPendingByLotsBids =
            mapPendingBids.entrySet().stream()
                          .filter(e -> containsAny(e.getValue().getRelatedLots(), lotsStr))
                          .peek(mapPendingBids::remove)
                          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        mapPendingByLotsBids.entrySet().forEach(e -> setStatus(e, WITHDRAWN));
        bidEntitiesWithdrawn.addAll(collectBids(mapPendingByLotsBids));
        mapPendingByLotsBids.forEach((key, value) -> organizationReferencesRs.addAll(value.getTenderers()));
        final int ruleMinBids = rulesService.getRulesMinBids(bidsDto.getCountry(), bidsDto.getPmd());
        final List<Bid> bidWithdrawnFilteredByRuleMap = filterByRule(mapPendingByLotsBids, ruleMinBids);
        bidWithdrawnFilteredByRuleMap.stream()
                                     .map(bid -> conversionService.convert(bid, BidWithdrawnRs.class))
                                     .forEach(responseBids::add);

        //collect pending bids with rule
        final List<Bid> pendingBidsWithoutInLots = filterByRule(mapPendingBids, ruleMinBids);
        pendingBidsWithoutInLots.forEach(bid -> responseBids.add(conversionService.convert(bid, BidWithdrawnRs.class)));
        pendingBidsWithoutInLots.forEach(bid -> organizationReferencesRs.addAll(bid.getTenderers()));

        final SubmissionPeriodEntity period = periodService.getPeriod(bidsDto.getOcid());
        final BidsWithdrawnRs.TenderPeriod tenderPeriod = new BidsWithdrawnRs.TenderPeriod(period.getEndDate());
        bidRepository.saveAll(bidEntitiesWithdrawn);
        return new BidsWithdrawnRs(tenderPeriod, organizationReferencesRs, responseBids);
    }

    @Override
    public BidWithdrawnRs updateStatusDetail(final String cpid, final String stage, final String bidId,
                                             final String awardStatus) {
        final UUID uuid = UUID.fromString(bidId);
        final BidEntity bidEntity = Optional.ofNullable(bidRepository.findByOcIdAndStageAndBidId(cpid, stage, uuid))
                                            .orElseThrow(() -> new ErrorException("No Bid"));
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        if (awardStatus.equals("unsuccessful")) {
            bid.setStatus(DISQUALIFIED);
            bid.setDate(LocalDateTime.now());
            bidEntity.setStatus(DISQUALIFIED);
        } else if (awardStatus.equals("active")) {
            bid.setStatus(VALID);
            bid.setDate(LocalDateTime.now());
            bidEntity.setStatus(VALID);
        }
        bidEntity.setJsonData(jsonUtil.toJson(bid));
        bidRepository.save(bidEntity);
        return conversionService.convert(bid, BidWithdrawnRs.class);
    }

    private Map<BidEntity, Bid> createBidCopy(final BidsCopyDto bidsCopyDto, final Map<BidEntity, Bid> entityBidMap) {
        final List<String> lotsDto = collectLots(bidsCopyDto.getLots());
        return entityBidMap.entrySet().stream()
                           .filter(e -> containsAny(e.getValue().getRelatedLots(), lotsDto))
                           .map(e -> copyBidEntity(e, bidsCopyDto.getStage()))
                           .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<BidEntity, Bid> filterByStatus(final List<BidEntity> bidEntities, final Bid.Status status) {
        return bidEntities.stream()
                          .filter(b -> b.getStatus() == status)
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
        final BiPredicate<List<OrganizationReference>, List<OrganizationReference>> isTenderersSame =
            isTenderersSameBiPredicate();
        final List<OrganizationReference> bidRequestTenderers = bidRequest.getBid().getTenderers();
        return bids.stream()
                   .filter(b -> b.getRelatedLots().containsAll(bidRequestRelatedLots))
                   .anyMatch(b -> isTenderersSame.test(b.getTenderers(), bidRequestTenderers));
    }

    private List<Bid> getCollectedBids(final List<BidEntity> bidEntities) {
        return bidEntities.stream()
                          .map(b -> jsonUtil.toObject(Bid.class, b.getJsonData()))
                          .collect(toList());
    }

    private BiPredicate<List<OrganizationReference>, List<OrganizationReference>> isTenderersSameBiPredicate() {
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
        final Bid.Status newStatus = INVITED;
        newBidEntity.setStatus(newStatus);
        final LocalDateTime dateTimeNow = LocalDateTime.now();
        newBidEntity.setCreatedDate(dateTimeNow);
        newBidEntity.setPendingDate(null);
        final Bid oldBid = entrySet.getValue();
        final Bid newBid = new Bid(oldBid.getId(), dateTimeNow, newStatus, null, oldBid.getTenderers(), null, null,
            oldBid.getRelatedLots());
        newBidEntity.setJsonData(jsonUtil.toJson(newBid));
        newBidEntity.setOwner(oldBidEntity.getOwner());
        return new AbstractMap.SimpleEntry<>(newBidEntity, newBid);
    }

    private BidEntity createNewBidEntity(final BidRequestDto requestDto) {
        final LocalDateTime dateTimeNow = LocalDateTime.now();
        requestDto.getBid().setDate(dateTimeNow);
        requestDto.getBid().setId(UUIDs.timeBased().toString());
        requestDto.getBid().setStatus(PENDING);
        final BidEntity bidEntity = conversionService.convert(requestDto, BidEntity.class);
        bidEntity.setStatus(PENDING);
        bidEntity.setPendingDate(dateTimeNow);
        bidEntity.setCreatedDate(dateTimeNow);
        bidEntity.setJsonData(jsonUtil.toJson(requestDto.getBid()));
        return bidEntity;
    }

    private BidEntity updateBidEntity(final BidRequestDto requestDto) {
        final BidEntity oldBidEntity =
            Optional.ofNullable(bidRepository.findByOcIdAndStageAndBidIdAndBidToken(requestDto.getOcid(),
                requestDto.getStage(), UUID.fromString(requestDto.getBid().getId()),
                UUID.fromString(requestDto.getBidToken())))
                    .orElseThrow(() -> new ErrorException("Don't have data."));
        final BidEntity bidEntity = updateBidEntity(requestDto, oldBidEntity);
        bidRepository.save(bidEntity);
        return bidEntity;
    }

    private BidEntity updateBidEntity(final BidRequestDto requestDto, final BidEntity oldBidEntity) {
        final BidEntity convertedBidEntity = conversionService.convert(requestDto, BidEntity.class);
        requestDto.getBid().setDate(LocalDateTime.now());
        convertedBidEntity.setJsonData(jsonUtil.toJson(requestDto.getBid()));
        convertedBidEntity.setCreatedDate(oldBidEntity.getCreatedDate());
        convertedBidEntity.setPendingDate(isSetPending(requestDto, oldBidEntity) ?
                                          LocalDateTime.now() :
                                          oldBidEntity.getPendingDate());
        return convertedBidEntity;
    }

    private boolean isSetPending(final BidRequestDto requestDto, final BidEntity oldBidEntity) {
        return oldBidEntity.getPendingDate() == null &&
            oldBidEntity.getStatus() != PENDING &&
            requestDto.getBid().getStatus() == PENDING;
    }

    private CommonBidResponse createBidResponse(final BidEntity bid) {
        final CommonBidResponse commonBidResponse = conversionService.convert(bid, CommonBidResponse.class);
        commonBidResponse.setBid(jsonUtil.toObject(Bid.class, bid.getJsonData()));
        return commonBidResponse;
    }

    private List<String> collectLots(final LotsDto bidsCopyDto) {
        return collectLots(bidsCopyDto.getLots());
    }

    private List<String> collectLots(final List<LotDto> bidsCopyDto) {
        return bidsCopyDto.stream()
                          .map(LotDto::getId)
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

    private BidsSelectionResponse.Bid convertBids(final BidEntity bidEntity) {
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        final BidsSelectionResponse.Bid bidSelection = conversionService.convert(bid, BidsSelectionResponse.Bid.class);
        bidSelection.setCreateDate(bidEntity.getCreatedDate());
        bidSelection.setPendingDate(bidEntity.getPendingDate());
        return bidSelection;
    }

    private List<BidEntity> pendingFilter(final List<BidEntity> bids) {
        return bids.stream()
                   .filter(b -> b.getStatus() == PENDING)
                   .collect(toList());
    }

    // TODO: 11.01.18 refactor this
    private List<Bid> filterByRule(final Map<BidEntity, Bid> bidMap, final int ruleMinBids) {
        final Map<Bid, Long> collect = bidMap.entrySet().stream()
                                             .map(Map.Entry::getValue)
                                             .collect(groupingBy(identity(), counting()));
        return collect.entrySet().stream()
                      .filter(e -> ruleMinBids > e.getValue())
                      .map(Map.Entry::getKey)
                      .collect(toList());
    }

    private List<BidEntity> collectBids(Map<BidEntity, Bid> mapBids) {
        return mapBids.entrySet().stream()
                      .map(this::setJsonData)
                      .collect(toList());
    }

    private BidEntity setJsonData(Map.Entry<BidEntity, Bid> entry) {
        entry.getKey().setJsonData(jsonUtil.toJson(entry.getValue()));
        return entry.getKey();
    }

    private Map.Entry<BidEntity, Bid> setStatus(final Map.Entry<BidEntity, Bid> entry, final Bid.Status status) {
        entry.getKey().setStatus(status);
        entry.getValue().setStatus(status);
        entry.getValue().setDate(LocalDateTime.now());
        return entry;
    }
}

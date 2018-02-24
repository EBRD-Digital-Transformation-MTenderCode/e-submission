package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Strings;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.LotDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.dto.response.*;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.entity.PeriodEntity;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.DateUtil;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static com.procurement.submission.model.ocds.Bid.Status.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Service
public class BidServiceImpl implements BidService {
    private final PeriodService periodService;
    private final BidRepository bidRepository;
    private final ConversionService conversionService;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final RulesService rulesService;

    public BidServiceImpl(final PeriodService periodService,
                          final BidRepository bidRepository,
                          final ConversionService conversionService,
                          final JsonUtil jsonUtil,
                          final DateUtil dateUtil,
                          final RulesService rulesService) {
        this.periodService = periodService;
        this.bidRepository = bidRepository;
        this.conversionService = conversionService;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.rulesService = rulesService;
    }

    @Override
    public ResponseDto createBid(final String cpId, final String stage, final String owner, final Bid bidDto) {
        periodService.checkCurrentDateInPeriod(cpId, stage);
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (!bidEntities.isEmpty()) {
            checkTenderers(bidEntities, bidDto);
        }
        processTenderers(bidDto);
        bidDto.setDate(dateUtil.localNowUTC());
        bidDto.setId(UUIDs.timeBased().toString());
        bidDto.setStatus(PENDING);
        final BidEntity entity = getNewBidEntity(cpId, stage, owner, bidDto);
        bidRepository.save(entity);
        return getResponseDto(entity.getToken().toString(), bidDto);
    }

    private void processTenderers(final Bid bidDto) {
        bidDto.getTenderers().forEach(t -> t.setId(t.getIdentifier().getScheme() + "-" + t.getIdentifier().getId()));
    }


    @Override
    public ResponseDto updateBid(final String cpId,
                                 final String stage,
                                 final String token,
                                 final String owner,
                                 final Bid bidDto) {
        periodService.checkCurrentDateInPeriod(cpId, stage);
        updateBidEntity(cpId, stage, token, owner, bidDto);
        return getResponseDto(token, bidDto);
    }

    @Override
    public ResponseDto copyBids(final String cpId,
                                final String stage,
                                final String previousStage,
                                final LotsDto lots) {
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, previousStage);
        if (bidEntities.isEmpty()) {
            throw new ErrorException("Bids not found.");
        }
        final Map<BidEntity, Bid> entityBidMap = filterByStatus(bidEntities, PENDING.value());
        final Map<BidEntity, Bid> newBidsMap = createBidCopy(lots, entityBidMap, stage);
        bidRepository.saveAll(newBidsMap.keySet());
        final List<Bid> bids = new ArrayList<>(newBidsMap.values());
        return new ResponseDto<>(true, null, new BidsCopyResponse(bids));
    }

    @Override
    public ResponseDto getBids(final String cpId,
                               final String stage,
                               final String country,
                               final String pmd) {
        periodService.checkIsPeriodExpired(cpId, stage);
        final List<BidEntity> pendingBids = pendingFilter(bidRepository.findAllByCpIdAndStage(cpId, stage));
        final int rulesMinBids = rulesService.getRulesMinBids(country, pmd);
        if (pendingBids.size() < rulesMinBids) {
            throw new ErrorException("Bids with status PENDING are less minimum count of bids.");
        }
        final List<BidsSelectionResponse.Bid> responseBids = pendingBids.stream()
                .map(this::convertBids)
                .collect(toList());
        return new ResponseDto<>(true, null, new BidsSelectionResponse(responseBids));
    }

    @Override
    public ResponseDto updateBidsByLots(final String cpId,
                                        final String stage,
                                        final String country,
                                        final String pmd,
                                        final LotsDto unsuccessfulLots) {
        final List<BidEntity> allBidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (allBidEntities.isEmpty()) {
            throw new ErrorException("Bids not found.");
        }
        //collect invited bids
        final Map<BidEntity, Bid> mapInvitedBids = filterByStatus(allBidEntities, INVITED.value());
        mapInvitedBids.entrySet().forEach(e -> setStatus(e, WITHDRAWN));
        final List<BidEntity> bidEntitiesWithdrawn = collectBids(mapInvitedBids);
        final Set<OrganizationReference> organizationReferencesRs = new HashSet<>();
        final List<BidUpdate> responseBids = new ArrayList<>();
        mapInvitedBids.forEach((key, value) -> organizationReferencesRs.addAll(value.getTenderers()));
        mapInvitedBids.entrySet().stream()
                .map(e -> conversionService.convert(e.getValue(), BidUpdate.class))
                .forEach(responseBids::add);

        //collect pending bids by lost and with rule
        final Map<BidEntity, Bid> mapPendingBids = filterByStatus(allBidEntities, PENDING.value());
        final List<String> lotsStr = collectLots(unsuccessfulLots.getLots());
        final Map<BidEntity, Bid> mapPendingByLotsBids =
                mapPendingBids.entrySet().stream()
                        .filter(e -> containsAny(e.getValue().getRelatedLots(), lotsStr))
                        .peek(mapPendingBids::remove)
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        mapPendingByLotsBids.entrySet().forEach(e -> setStatus(e, WITHDRAWN));
        bidEntitiesWithdrawn.addAll(collectBids(mapPendingByLotsBids));
        mapPendingByLotsBids.forEach((key, value) -> organizationReferencesRs.addAll(value.getTenderers()));
        final int ruleMinBids = rulesService.getRulesMinBids(country, pmd);
        final List<Bid> bidWithdrawnFilteredByRuleMap = filterByRule(mapPendingByLotsBids, ruleMinBids);
        bidWithdrawnFilteredByRuleMap.stream()
                .map(bid -> conversionService.convert(bid, BidUpdate.class))
                .forEach(responseBids::add);

        //collect pending bids with rule
        final List<Bid> pendingBidsWithoutInLots = filterByRule(mapPendingBids, ruleMinBids);
        pendingBidsWithoutInLots.forEach(bid -> responseBids.add(conversionService.convert(bid, BidUpdate.class)));
        pendingBidsWithoutInLots.forEach(bid -> organizationReferencesRs.addAll(bid.getTenderers()));

        bidRepository.saveAll(bidEntitiesWithdrawn);

        final PeriodEntity period = periodService.getPeriod(cpId, stage);
        final BidsUpdateStatusResponse.TenderPeriod tenderPeriod =
                new BidsUpdateStatusResponse.TenderPeriod(period.getStartDate(), period.getEndDate());
        return new ResponseDto<>(true,
                null,
                new BidsUpdateStatusResponse(tenderPeriod, organizationReferencesRs, responseBids));
    }

    @Override
    public ResponseDto updateStatusDetail(final String cpId,
                                          final String stage,
                                          final String bidId,
                                          final String awardStatus) {
        final BidEntity bidEntity = Optional.ofNullable(bidRepository.findByCpIdAndStageAndBidId(cpId, stage,
                UUID.fromString(bidId)))
                .orElseThrow(() -> new ErrorException("Bid not found."));
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        if (awardStatus.equals("unsuccessful")) {
            bid.setStatus(DISQUALIFIED);
            bid.setDate(dateUtil.localNowUTC());
            bidEntity.setStatus(DISQUALIFIED.value());
        } else if (awardStatus.equals("active")) {
            bid.setStatus(VALID);
            bid.setDate(dateUtil.localNowUTC());
            bidEntity.setStatus(VALID.value());
        } else {
            throw new ErrorException("Invalid Award status.");
        }

        bidEntity.setJsonData(jsonUtil.toJson(bid));
        bidRepository.save(bidEntity);
        final BidUpdate bidUpdate = conversionService.convert(bid, BidUpdate.class);
        return new ResponseDto<>(true, null, bidUpdate);
    }

    @Override
    public ResponseDto setFinalStatuses(final String cpId, final String stage) {
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (bidEntities.isEmpty()) {
            throw new ErrorException("Bids not found.");
        }
        final Map<BidEntity, Bid> bidMap = filterByStatus(bidEntities, PENDING.value());
        final Map<BidEntity, Bid> bidMapWithStatus =
                bidMap.entrySet().stream()
                        .map(e -> setStatus(e, Bid.Status.valueOf(e.getValue().getStatusDetail().toString())))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        bidMapWithStatus.forEach((key, value) -> value.setStatusDetail(null));
        bidMapWithStatus.forEach((key, value) -> key.setJsonData(jsonUtil.toJson(value)));
        final List<BidUpdate> bidUpdateList = bidMapWithStatus.entrySet().stream()
                .map(e -> conversionService.convert(e.getValue(), BidUpdate.class))
                .collect(toList());
        return new ResponseDto<>(true,
                null,
                new BidsUpdateFinalStatusResponse(bidUpdateList));
    }

    private void checkTenderers(final List<BidEntity> bidEntities, final Bid bidDto) {
        if (isExistTenderers(getCollectedBids(bidEntities), bidDto)) {
            throw new ErrorException("We have Bid with this Lots and Tenderers");
        }
    }

    private List<Bid> getCollectedBids(final List<BidEntity> bidEntities) {
        return bidEntities.stream()
                .map(b -> jsonUtil.toObject(Bid.class, b.getJsonData()))
                .collect(toList());
    }

    private boolean isExistTenderers(final List<Bid> bids, final Bid bidDto) {
        final List<String> bidRequestRelatedLots = bidDto.getRelatedLots();
        final BiPredicate<List<OrganizationReference>, List<OrganizationReference>> isTenderersSame =
                isTenderersSameBiPredicate();
        final List<OrganizationReference> bidRequestTenderers = bidDto.getTenderers();
        return bids.stream()
                .filter(b -> b.getRelatedLots().containsAll(bidRequestRelatedLots))
                .anyMatch(b -> isTenderersSame.test(b.getTenderers(), bidRequestTenderers));
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

    private BidEntity getNewBidEntity(final String cpId, final String stage, final String owner, final Bid bidDto) {
        final BidEntity bidEntity = new BidEntity();
        bidEntity.setCpId(cpId);
        bidEntity.setStage(stage);
        bidEntity.setOwner(owner);
        bidEntity.setStatus(bidDto.getStatus().value());
        bidEntity.setBidId(UUID.fromString(bidDto.getId()));
        bidEntity.setToken(UUIDs.timeBased());
        bidEntity.setPendingDate(bidDto.getDate());
        bidEntity.setCreatedDate(bidDto.getDate());
        bidEntity.setJsonData(jsonUtil.toJson(bidDto));
        return bidEntity;
    }

    private void updateBidEntity(final String cpId,
                                 final String stage,
                                 final String token,
                                 final String owner,
                                 final Bid bidDto) {
        if (Strings.isNullOrEmpty(bidDto.getId())) throw new ErrorException("Invalid bid id.");
        final BidEntity entity = Optional
                .ofNullable(bidRepository.findByCpIdAndStageAndBidIdAndToken(cpId, stage,
                        UUID.fromString(bidDto.getId()), UUID.fromString(token)))
                .orElseThrow(() -> new ErrorException("Bid not found."));
        if (!entity.getOwner().equals(owner)) throw new ErrorException("Invalid owner.");
        bidDto.setDate(dateUtil.localNowUTC());
        setPendingDate(bidDto, entity);
        entity.setJsonData(jsonUtil.toJson(bidDto));
        bidRepository.save(entity);
    }

    private void setPendingDate(final Bid bidDto, final BidEntity entity) {
        if (entity.getPendingDate() == null
                && entity.getStatus() != PENDING.value()
                && bidDto.getStatus() == PENDING) {
            entity.setPendingDate(bidDto.getDate());
        }
    }

    private ResponseDto<BidResponseDto> getResponseDto(final String token, final Bid bid) {
        final BidResponseDto responseDto = new BidResponseDto(token, bid);
        return new ResponseDto<>(true, null, responseDto);
    }

    private Map<BidEntity, Bid> createBidCopy(final LotsDto lotsDto,
                                              final Map<BidEntity, Bid> entityBidMap,
                                              final String stage) {
        final List<String> lots = collectLots(lotsDto.getLots());
        return entityBidMap.entrySet().stream()
                .filter(e -> containsAny(e.getValue().getRelatedLots(), lots))
                .map(e -> copyBidEntity(e, stage))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<BidEntity, Bid> copyBidEntity(final Map.Entry<BidEntity, Bid> entrySet, final String stage) {
        final BidEntity oldBidEntity = entrySet.getKey();
        final BidEntity newBidEntity = new BidEntity();
        newBidEntity.setCpId(oldBidEntity.getCpId());
        newBidEntity.setStage(stage);
        newBidEntity.setBidId(oldBidEntity.getBidId());
        newBidEntity.setToken(oldBidEntity.getToken());
        final Bid.Status newStatus = INVITED;
        newBidEntity.setStatus(newStatus.value());
        final LocalDateTime dateTimeNow = dateUtil.localNowUTC();
        newBidEntity.setCreatedDate(dateTimeNow);
        newBidEntity.setPendingDate(null);
        final Bid oldBid = entrySet.getValue();
        final Bid newBid = new Bid(oldBid.getId(), dateTimeNow, newStatus, null, oldBid.getTenderers(), null, null,
                oldBid.getRelatedLots());
        newBidEntity.setJsonData(jsonUtil.toJson(newBid));
        newBidEntity.setOwner(oldBidEntity.getOwner());
        return new AbstractMap.SimpleEntry<>(newBidEntity, newBid);
    }

    private List<BidEntity> pendingFilter(final List<BidEntity> bids) {
        return bids.stream()
                .filter(b -> b.getStatus() == PENDING.value())
                .collect(toList());
    }

    private BidsSelectionResponse.Bid convertBids(final BidEntity bidEntity) {
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        final BidsSelectionResponse.Bid bidSelection = conversionService.convert(bid, BidsSelectionResponse.Bid.class);
        bidSelection.setCreateDate(bidEntity.getCreatedDate());
        bidSelection.setPendingDate(bidEntity.getPendingDate());
        return bidSelection;
    }

    private List<BidEntity> collectBids(final Map<BidEntity, Bid> mapBids) {
        return mapBids.entrySet().stream()
                .map(this::setJsonData)
                .collect(toList());
    }

    private BidEntity setJsonData(final Map.Entry<BidEntity, Bid> entry) {
        entry.getKey().setJsonData(jsonUtil.toJson(entry.getValue()));
        return entry.getKey();
    }

    private List<String> collectLots(final List<LotDto> lots) {
        return lots.stream()
                .map(LotDto::getId)
                .collect(toList());
    }

    private List<Bid> filterByRule(final Map<BidEntity, Bid> bidMap, final int ruleMinBids) {
        final Map<Bid, Long> collect = bidMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(groupingBy(identity(), counting()));
        return collect.entrySet().stream()
                .filter(e -> ruleMinBids > e.getValue())
                .map(Map.Entry::getKey)
                .collect(toList());
    }

    private Map<BidEntity, Bid> filterByStatus(final List<BidEntity> bidEntities, final String status) {
        return bidEntities.stream()
                .filter(b -> b.getStatus() == status)
                .collect(HashMap<BidEntity, Bid>::new,
                        (k, v) -> k.put(v, jsonUtil.toObject(Bid.class, v.getJsonData())),
                        HashMap::putAll);
    }

    private <T> boolean containsAny(final Collection<T> src, final Collection<T> dest) {
        for (final T value : dest) {
            if (src.contains(value)) {
                System.out.println(value);
                return true;
            }
        }
        return false;
    }


    private Map.Entry<BidEntity, Bid> setStatus(final Map.Entry<BidEntity, Bid> entry, final Bid.Status status) {
        entry.getKey().setStatus(status.value());
        entry.getValue().setStatus(status);
        entry.getValue().setDate(LocalDateTime.now());
        return entry;
    }

}

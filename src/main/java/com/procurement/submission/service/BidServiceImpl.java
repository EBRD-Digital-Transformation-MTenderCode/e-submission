package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.base.Strings;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.exception.ErrorType;
import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.request.AwardStatusDetails;
import com.procurement.submission.model.dto.request.LotDto;
import com.procurement.submission.model.dto.request.LotsDto;
import com.procurement.submission.model.dto.request.UnsuccessfulLotsDto;
import com.procurement.submission.model.dto.response.*;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.entity.PeriodEntity;
import com.procurement.submission.model.ocds.*;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.DateUtil;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.*;

@Service
public class BidServiceImpl implements BidService {

    private final PeriodService periodService;
    private final BidRepository bidRepository;
    private final JsonUtil jsonUtil;
    private final DateUtil dateUtil;
    private final RulesService rulesService;

    public BidServiceImpl(final PeriodService periodService,
                          final BidRepository bidRepository,
                          final JsonUtil jsonUtil,
                          final DateUtil dateUtil,
                          final RulesService rulesService) {
        this.periodService = periodService;
        this.bidRepository = bidRepository;
        this.jsonUtil = jsonUtil;
        this.dateUtil = dateUtil;
        this.rulesService = rulesService;
    }

    @Override
    public ResponseDto createBid(final String cpId, final String stage, final String owner, final Bid bidDto) {
        periodService.checkCurrentDateInPeriod(cpId, stage);
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (!bidEntities.isEmpty()) checkTenderers(bidEntities, bidDto);
        processTenderers(bidDto);
        bidDto.setDate(dateUtil.localNowUTC());
        bidDto.setId(UUIDs.timeBased().toString());
        bidDto.setStatus(Status.PENDING);
        bidDto.setStatusDetails(StatusDetails.EMPTY);
        final BidEntity entity = getNewBidEntity(cpId, stage, owner, bidDto);
        bidRepository.save(entity);
        return getResponseDto(entity.getToken().toString(), bidDto);
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
                                final String newStage,
                                final String previousStage,
                                final LocalDateTime startDate,
                                final LocalDateTime endDate,
                                final LotsDto lotsDto) {
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, previousStage);
        if (bidEntities.isEmpty()) throw new ErrorException(ErrorType.BID_NOT_FOUND);
        periodService.savePeriod(cpId, newStage, startDate, endDate);
        final Map<BidEntity, Bid> validBids = filterForNewStage(bidEntities, lotsDto);
        final Map<BidEntity, Bid> newBids = createBidCopy(lotsDto, validBids, newStage);
        bidRepository.saveAll(newBids.keySet());
        final List<Bid> bids = new ArrayList<>(newBids.values());
        return new ResponseDto<>(true, null,
                new BidsCopyResponseDto(new Bids(bids), new Period(startDate, endDate)));
    }

    @Override
    public ResponseDto getPendingBids(final String cpId,
                                      final String stage,
                                      final String country,
                                      final String pmd) {
        periodService.checkIsPeriodExpired(cpId, stage);
        final List<BidEntity> pendingBids = pendingFilter(bidRepository.findAllByCpIdAndStage(cpId, stage));
        final int minNumberOfBids = rulesService.getRulesMinBids(country, pmd);
        final List<Bid> bids = getBidsFromEntities(pendingBids);
        final List<String> relatedLotsFromBids = getRelatedLotsIdFromBids(bids);
        final Map<String, Long> uniqueLots = getUniqueLots(relatedLotsFromBids);
        final List<String> successfulLots = getSuccessfulLots(uniqueLots, minNumberOfBids);
        final List<Bid> successfulBids = getSuccessfulBids(bids, successfulLots);
        return new ResponseDto<>(true, null, new BidsSelectionResponseDto(successfulBids));
    }

    @Override
    public ResponseDto updateStatus(final String cpId,
                                    final String stage,
                                    final String country,
                                    final String pmd,
                                    final UnsuccessfulLotsDto unsuccessfulLots) {
        /*get all bids entities from db*/
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (bidEntities.isEmpty()) throw new ErrorException(ErrorType.BID_NOT_FOUND);
        /*get all bids from entities*/
        final List<Bid> bids = getBidsFromEntities(bidEntities);
        /*set status WITHDRAWN for bids in INVITED status*/
        final List<Bid> updatedBids = new ArrayList<>();
        bids.stream()
                .filter(bid -> bid.getStatus().equals(Status.INVITED))
                .forEach(bid -> {
                    bid.setDate(dateUtil.localNowUTC());
                    bid.setStatus(Status.WITHDRAWN);
                    updatedBids.add(bid);
                });
        /*set status WITHDRAWN for bids with unsuccessful lots*/
        final Set<String> lotsStr = collectLots(unsuccessfulLots.getLots());
        bids.stream()
                .filter(bid -> containsAny(bid.getRelatedLots(), lotsStr))
                .forEach(bid -> {
                    bid.setDate(dateUtil.localNowUTC());
                    bid.setStatus(Status.WITHDRAWN);
                    updatedBids.add(bid);
                });
        /*get entities for update*/
        final List<BidEntity> updatedBidEntities = getUpdatedBidEntities(bidEntities, updatedBids);
        /*save updated entities*/
        bidRepository.saveAll(updatedBidEntities);
//        /*get tenderers from bids*/
//        final Set<OrganizationReference> tenderers = new HashSet<>();
//        bids.forEach(bid -> tenderers.addAll(bid.getTenderers()));
        /*get tender period from db*/
        final PeriodEntity period = periodService.getPeriod(cpId, stage);
        final Period tenderPeriod = new Period(period.getStartDate(), period.getEndDate());

        return new ResponseDto<>(true, null, new BidsUpdateStatusResponseDto(tenderPeriod, bids));
    }

    @Override
    public ResponseDto updateStatusDetails(final String cpId,
                                           final String stage,
                                           final String bidId,
                                           final AwardStatusDetails awardStatusDetails) {
        final BidEntity bidEntity = Optional.ofNullable(
                bidRepository.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId)))
                .orElseThrow(() -> new ErrorException(ErrorType.BID_NOT_FOUND));
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        switch (awardStatusDetails) {
            case EMPTY: {
                bid.setStatusDetails(StatusDetails.EMPTY);
                break;
            }
            case ACTIVE: {
                bid.setStatusDetails(StatusDetails.VALID);
                break;
            }
            case UNSUCCESSFUL: {
                bid.setStatusDetails(StatusDetails.DISQUALIFIED);
                break;
            }
        }
        bid.setDate(dateUtil.localNowUTC());
        bidEntity.setJsonData(jsonUtil.toJson(bid));
        bidRepository.save(bidEntity);
        return new ResponseDto<>(true, null, new BidsUpdateStatusDetailsResponseDto(getBidUpdate(bid)));
    }

    @Override
    public ResponseDto setFinalStatuses(final String cpId, final String stage) {
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (bidEntities.isEmpty()) throw new ErrorException(ErrorType.BID_NOT_FOUND);
        final List<Bid> bids = getBidsFromEntities(bidEntities);
        //set status from statusDetails
        for (final Bid bid : bids) {
            if (bid.getStatus().equals(Status.PENDING)) {
                bid.setDate(dateUtil.localNowUTC());
                bid.setStatus(Status.fromValue(bid.getStatusDetails().value()));
                bid.setStatusDetails(StatusDetails.EMPTY);
            }
        }
        /*get entities for update*/
        final List<BidEntity> updatedBidEntities = getUpdatedBidEntities(bidEntities, bids);
        /*save updated entities*/
        bidRepository.saveAll(updatedBidEntities);
        return new ResponseDto<>(true, null, new BidsFinalStatusResponseDto(bids));
    }

    private List<BidEntity> getUpdatedBidEntities(final List<BidEntity> bidEntities, final List<Bid> bids) {
        final List<BidEntity> updatedBidEntities = new ArrayList<>();
        bids.forEach(bid ->
                bidEntities.stream()
                        .filter(entity -> entity.getBidId().toString().equals(bid.getId()))
                        .forEach(entity -> {
                            entity.setStatus(bid.getStatus().value());
                            entity.setJsonData(jsonUtil.toJson(bid));
                            updatedBidEntities.add(entity);
                        })
        );
        return updatedBidEntities;
    }

    private List<String> getRelatedLotsIdFromBids(final List<Bid> bids) {
        return bids.stream()
                .flatMap(bid -> bid.getRelatedLots().stream())
                .collect(Collectors.toList());
    }

    private Map<String, Long> getUniqueLots(final List<String> lots) {
        return lots.stream().collect(groupingBy(Function.identity(), Collectors.counting()));
    }

    private List<String> getSuccessfulLots(final Map<String, Long> uniqueLots,
                                           final int minNumberOfBids) {
        return uniqueLots.entrySet()
                .stream()
                .filter(map -> map.getValue() >= minNumberOfBids)
                .map(map -> map.getKey())
                .collect(Collectors.toList());
    }

    private List<Bid> getSuccessfulBids(final List<Bid> bids, final List<String> successfulLots) {
        final List<Bid> successfulBids = new ArrayList<>();
        bids.forEach(bid ->
                bid.getRelatedLots()
                        .stream()
                        .filter(successfulLots::contains)
                        .map(lot -> bid)
                        .forEach(successfulBids::add));
        return successfulBids;
    }

    private void processTenderers(final Bid bidDto) {
        bidDto.getTenderers().forEach(t -> t.setId(t.getIdentifier().getScheme() + "-" + t.getIdentifier().getId()));
    }

    private void checkTenderers(final List<BidEntity> bidEntities, final Bid bidDto) {
        if (isExistTenderers(getBidsFromEntities(bidEntities), bidDto)) {
            throw new ErrorException(ErrorType.BID_ALREADY_WITH_LOT);
        }
    }

    private List<Bid> getBidsFromEntities(final List<BidEntity> bidEntities) {
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
        return (tenderersFromDb, tenderersFromRequest) ->
                tenderersFromDb.size() == tenderersFromRequest.size()
                        && tenderersFromDb.containsAll(tenderersFromRequest);
    }

    private void updateBidEntity(final String cpId,
                                 final String stage,
                                 final String token,
                                 final String owner,
                                 final Bid bidDto) {
        if (Strings.isNullOrEmpty(bidDto.getId())) throw new ErrorException(ErrorType.INVALID_ID);
        final BidEntity entity = Optional
                .ofNullable(bidRepository.findByCpIdAndStageAndBidIdAndToken(cpId, stage,
                        UUID.fromString(bidDto.getId()), UUID.fromString(token)))
                .orElseThrow(() -> new ErrorException(ErrorType.BID_NOT_FOUND));
        if (!entity.getOwner().equals(owner)) throw new ErrorException(ErrorType.INVALID_OWNER);
        bidDto.setDate(dateUtil.localNowUTC());
        entity.setJsonData(jsonUtil.toJson(bidDto));
        bidRepository.save(entity);
    }

    private ResponseDto<BidResponseDto> getResponseDto(final String token, final Bid bid) {
        return new ResponseDto<>(true, null, new BidResponseDto(token, bid.getId(), bid));
    }

    private Map<BidEntity, Bid> createBidCopy(final LotsDto lotsDto,
                                              final Map<BidEntity, Bid> entityBidMap,
                                              final String stage) {
        final Set<String> lots = collectLots(lotsDto.getLots());
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
        final Status newStatus = Status.INVITED;
        newBidEntity.setStatus(newStatus.value());
        final LocalDateTime dateTimeNow = dateUtil.localNowUTC();
        newBidEntity.setCreatedDate(dateTimeNow);
        final Bid oldBid = entrySet.getValue();
        final Bid newBid = new Bid(
                oldBid.getId(),
                dateTimeNow,
                newStatus,
                StatusDetails.EMPTY,
                oldBid.getTenderers(),
                null,
                null,
                oldBid.getRelatedLots());
        newBidEntity.setJsonData(jsonUtil.toJson(newBid));
        newBidEntity.setOwner(oldBidEntity.getOwner());
        return new AbstractMap.SimpleEntry<>(newBidEntity, newBid);
    }

    private List<BidEntity> pendingFilter(final List<BidEntity> bids) {
        return bids.stream()
                .filter(b -> b.getStatus().equals(Status.PENDING.value()))
                .collect(toList());
    }

    private Map<BidEntity, Bid> filterForNewStage(final List<BidEntity> bidEntities, final LotsDto lotsDto) {
        final Map<BidEntity, Bid> validBids = new HashMap<>();
        final Set<String> lotsID = collectLots(lotsDto.getLots());
        bidEntities.forEach(bidEntity -> {
            final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
            if (bid.getStatus().equals(Status.VALID) && bid.getStatusDetails().equals(StatusDetails.EMPTY))
                for (final String lot : bid.getRelatedLots()) {
                    if (lotsID.contains(lot))
                        validBids.put(bidEntity, bid);
                }
        });
        return validBids;
    }

    private Set<String> collectLots(final List<LotDto> lots) {
        return lots.stream().map(LotDto::getId).collect(toSet());
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

    public BidUpdateDto getBidUpdate(final Bid bid) {
        return new BidUpdateDto(bid.getId(),
                bid.getDate(),
                bid.getStatus(),
                bid.getStatusDetails(),
                createTenderers(bid.getTenderers()),
                bid.getValue(),
                bid.getDocuments(),
                bid.getRelatedLots());
    }

    private List<OrganizationReferenceDto> createTenderers(final List<OrganizationReference> tenderers) {
        return tenderers.stream()
                .map(t -> new OrganizationReferenceDto(t.getId(), t.getName()))
                .collect(toList());
    }

    private BidEntity getNewBidEntity(final String cpId, final String stage, final String owner, final Bid bidDto) {
        final BidEntity bidEntity = new BidEntity();
        bidEntity.setCpId(cpId);
        bidEntity.setStage(stage);
        bidEntity.setOwner(owner);
        bidEntity.setStatus(bidDto.getStatus().value());
        bidEntity.setBidId(UUID.fromString(bidDto.getId()));
        bidEntity.setToken(UUIDs.random());
        bidEntity.setCreatedDate(bidDto.getDate());
        bidEntity.setJsonData(jsonUtil.toJson(bidDto));
        return bidEntity;
    }
}

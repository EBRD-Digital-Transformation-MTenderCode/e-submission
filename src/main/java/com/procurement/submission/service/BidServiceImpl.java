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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class BidServiceImpl implements BidService {

    private static final String BID_NOT_FOUND = "Bid not found.";
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
            throw new ErrorException(BID_NOT_FOUND);
        }
        final Map<BidEntity, Bid> entityBidMap = filterByStatus(bidEntities, PENDING.value());
        final Map<BidEntity, Bid> newBidsMap = createBidCopy(lots, entityBidMap, stage);
        bidRepository.saveAll(newBidsMap.keySet());
        final List<Bid> bids = new ArrayList<>(newBidsMap.values());
        return new ResponseDto<>(true, null, new BidsCopyResponse(bids));
    }

    @Override
    public ResponseDto getPendingBids(final String cpId,
                                      final String stage,
                                      final String country,
                                      final String pmd) {
        periodService.checkIsPeriodExpired(cpId, stage);
        final List<BidEntity> pendingBids = pendingFilter(bidRepository.findAllByCpIdAndStage(cpId, stage));
//        final int rulesMinBids = rulesService.getRulesMinBids(country, pmd);
//        if (pendingBids.size() < rulesMinBids) {
//            throw new ErrorException("Bids with status PENDING are less minimum count of bids.");
//        }
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
        /*get all bids entities from db*/
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (bidEntities.isEmpty()) {
            throw new ErrorException(BID_NOT_FOUND);
        }
        /*get all bids from entities*/
        final List<Bid> bids = getBidsFromEntities(bidEntities);
        /*set status WITHDRAWN for bids in INVITED status*/
        final List<Bid> updatedBids = new ArrayList<>();
        bids.stream()
                .filter(bid -> bid.getStatus().equals(INVITED))
                .forEach(bid -> {
                    bid.setStatus(WITHDRAWN);
                    updatedBids.add(bid);
                });
        /*set status WITHDRAWN for bids with unsuccessful lots*/
        final List<String> lotsStr = collectLots(unsuccessfulLots.getLots());
        bids.stream()
                .filter(bid -> containsAny(bid.getRelatedLots(), lotsStr))
                .forEach(bid -> {
                    bid.setStatus(WITHDRAWN);
                    updatedBids.add(bid);
                });
        /*get entities for update*/
        final List<BidEntity> updatedBidEntities = new ArrayList<>();
        updatedBids.forEach(bid ->
                bidEntities.stream()
                        .filter(entity -> entity.getBidId().toString().equals(bid.getId()))
                        .forEach(entity -> {
                            entity.setStatus(bid.getStatus().value());
                            entity.setJsonData(jsonUtil.toJson(bid));
                            updatedBidEntities.add(entity);
                        })
        );
        /*save updated entities*/
        bidRepository.saveAll(updatedBidEntities);
        /*get tenderers from bids*/
        final Set<OrganizationReference> tenderers = new HashSet<>();
        bids.forEach(bid -> tenderers.addAll(bid.getTenderers()));
        /*get tender period from db*/
        final PeriodEntity period = periodService.getPeriod(cpId, stage);
        final Period tenderPeriod = new Period(period.getStartDate(), period.getEndDate());

        return new ResponseDto<>(true, null,
                new BidsUpdateStatusResponse(tenderPeriod, tenderers, bids));
    }

    @Override
    public ResponseDto updateStatusDetails(final String cpId,
                                           final String stage,
                                           final String bidId,
                                           final String awardStatus) {
        final BidEntity bidEntity = Optional.ofNullable(
                bidRepository.findByCpIdAndStageAndBidId(cpId, stage, UUID.fromString(bidId)))
                .orElseThrow(() -> new ErrorException(BID_NOT_FOUND));
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        if (awardStatus.equals("unsuccessful")) {
            bid.setDate(dateUtil.localNowUTC());
            bid.setStatusDetails(Bid.StatusDetails.DISQUALIFIED);
            bidEntity.setStatus(Bid.StatusDetails.DISQUALIFIED.value());
        } else if (awardStatus.equals("active")) {
            bid.setDate(dateUtil.localNowUTC());
            bid.setStatusDetails(Bid.StatusDetails.VALID);
            bidEntity.setStatus(Bid.StatusDetails.VALID.value());
        } else {
            throw new ErrorException("Invalid Award status.");
        }

        bidEntity.setJsonData(jsonUtil.toJson(bid));
        bidRepository.save(bidEntity);
        final BidUpdate bidUpdate = conversionService.convert(bid, BidUpdate.class);
        return new ResponseDto<>(true, null, new BidsUpdateStatusDetailsResponse(bidUpdate));
    }

    @Override
    public ResponseDto setFinalStatuses(final String cpId, final String stage) {
        final List<BidEntity> bidEntities = bidRepository.findAllByCpIdAndStage(cpId, stage);
        if (bidEntities.isEmpty()) {
            throw new ErrorException(BID_NOT_FOUND);
        }
        final Map<BidEntity, Bid> bidMap = filterByStatus(bidEntities, PENDING.value());
        final Map<BidEntity, Bid> bidMapWithStatus =
                bidMap.entrySet().stream()
                        .map(e -> setStatus(e, Bid.Status.valueOf(e.getValue().getStatusDetails().toString())))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        bidMapWithStatus.forEach((key, value) -> value.setStatusDetails(null));
        bidMapWithStatus.forEach((key, value) -> key.setJsonData(jsonUtil.toJson(value)));
        final List<BidUpdate> bidUpdateList = bidMapWithStatus.entrySet().stream()
                .map(e -> conversionService.convert(e.getValue(), BidUpdate.class))
                .collect(toList());
        return new ResponseDto<>(true,
                null,
                new BidsUpdateFinalStatusResponse(bidUpdateList));
    }

    private void checkTenderers(final List<BidEntity> bidEntities, final Bid bidDto) {
        if (isExistTenderers(getBidsFromEntities(bidEntities), bidDto)) {
            throw new ErrorException("We have Bid with this Lots and Tenderers");
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
                .orElseThrow(() -> new ErrorException(BID_NOT_FOUND));
        if (!entity.getOwner().equals(owner)) throw new ErrorException("Invalid owner.");
        bidDto.setDate(dateUtil.localNowUTC());
        setPendingDate(bidDto, entity);
        entity.setJsonData(jsonUtil.toJson(bidDto));
        bidRepository.save(entity);
    }

    private void setPendingDate(final Bid bidDto, final BidEntity entity) {
        if (Objects.isNull(entity.getPendingDate())
                && !entity.getStatus().equals(PENDING.value())
                && bidDto.getStatus().equals(PENDING)) {
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
                .filter(b -> b.getStatus().equals(PENDING.value()))
                .collect(toList());
    }

    private BidsSelectionResponse.Bid convertBids(final BidEntity bidEntity) {
        final Bid bid = jsonUtil.toObject(Bid.class, bidEntity.getJsonData());
        final BidsSelectionResponse.Bid bidSelection = conversionService.convert(bid, BidsSelectionResponse.Bid.class);
        bidSelection.setCreatedDate(bidEntity.getCreatedDate());
        bidSelection.setPendingDate(bidEntity.getPendingDate());
        return bidSelection;
    }

    private List<String> collectLots(final List<LotDto> lots) {
        return lots.stream()
                .map(LotDto::getId)
                .collect(toList());
    }

    private Map<BidEntity, Bid> filterByStatus(final List<BidEntity> bidEntities, final String status) {
        return bidEntities.stream()
                .filter(b -> b.getStatus().equals(status))
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

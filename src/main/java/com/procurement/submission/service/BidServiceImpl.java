package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

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
        // TODO: 22.12.17  check that tenderers are not repeated in every bid
        final BidEntity bid = createNewBidEntity(bidRequest);
        bidRepository.save(bid);
        final BidResponse bidResponse = createBidResponse(bid);
        return bidResponse;
    }

    private BidEntity createNewBidEntity(final BidRequestDto requestDto) {
        requestDto.getBid().setId(UUIDs.timeBased().toString());
        final LocalDateTime dateTimeNow = LocalDateTime.now();
        requestDto.getBid().setDate(dateTimeNow);
        final BidEntity bidEntity = conversionService.convert(requestDto, BidEntity.class);
        bidEntity.setStatus(Bid.Status.PENDING);
        bidEntity.setPendingDate(dateTimeNow);
        bidEntity.setCreatedDate(dateTimeNow);
        bidEntity.setJsonData(jsonUtil.toJson(requestDto.getBid()));
        return bidEntity;
    }

    private BidResponse createBidResponse(final BidEntity bid) {
        final BidResponse bidResponse = conversionService.convert(bid, BidResponse.class);
        bidResponse.setBid(jsonUtil.toObject(Bid.class, bid.getJsonData()));
        return bidResponse;
    }
}

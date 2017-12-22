package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.BidAqpDto;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.BidStatus;
import com.procurement.submission.model.ocds.Document;
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
    public BidResponse createBid(final BidRequestDto bidRequest) {
        periodService.checkPeriod(bidRequest.getOcid());
        // TODO: 22.12.17  check that tenderers are not repeated id every bid
        final BidEntity bid = createBidEntity(bidRequest);
        bidRepository.save(bid);
        bidRequest.getBid().setId(UUIDs.timeBased().toString());
        bidRequest.getBid().setDate(LocalDateTime.now());

        final BidResponse bidResponse = conversionService.convert(bidRequest, BidResponse.class);
        return bidResponse;
    }

    private BidEntity createBidEntity(final BidRequestDto fullBid) {
        BidEntity bidEntity = conversionService.convert(fullBid, BidEntity.class);


        return bidEntity;
    }
}

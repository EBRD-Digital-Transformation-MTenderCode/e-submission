package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.exception.ErrorInsertException;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.repository.PeriodRepository;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BidServiceImpl implements BidService {
    private PeriodRepository periodRepository;
    private BidRepository bidRepository;
    private JsonUtil jsonUtil;

    public BidServiceImpl(PeriodRepository periodRepository,
                          BidRepository bidRepository,
                          JsonUtil jsonUtil) {
        this.periodRepository = periodRepository;
        this.bidRepository = bidRepository;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public void insertData(final QualificationOfferDto dataDto) {
        Objects.requireNonNull(dataDto);
        final LocalDateTime localDateTime = LocalDateTime.now();
        checkPeriod(localDateTime, dataDto.getOcid());
        convertDtoToEntity(dataDto.getOcid(), localDateTime, dataDto.getBid())
            .ifPresent(bid -> bidRepository.save(bid));
    }

    private void checkPeriod(final LocalDateTime localDateTime, final String ocid) {
        final SubmissionPeriodEntity periodEntity = periodRepository.getByOcId(ocid);
        boolean localDateTimeAfter = localDateTime.isAfter(periodEntity.getStartDate());
        boolean localDateTimeBefore = localDateTime.isBefore(periodEntity.getEndDate());
        if (!localDateTimeAfter && !localDateTimeBefore) {
            throw new ErrorInsertException("Not found date.");
        }
    }

    private Optional<BidEntity> convertDtoToEntity(String ocId,
                                                  LocalDateTime localDateTime,
                                                  BidQualificationDto bidDto) {
        Objects.requireNonNull(ocId);
        Objects.requireNonNull(bidDto);
        Objects.requireNonNull(localDateTime);
        BidEntity bidEntity = new BidEntity();
        bidEntity.setOcId(ocId);
        UUID bidId;
        if (Objects.isNull(bidDto.getId())) {
            bidId = UUIDs.timeBased();
            bidDto.setId(bidId.toString());
        } else {
            bidId = java.util.UUID.fromString(bidDto.getId());
        }
        if (Objects.isNull(bidDto.getDate())) {
            bidDto.setDate(localDateTime);
        }
        bidEntity.setStatus(bidDto.getStatus().value());
        bidEntity.setBidId(bidId);
        bidEntity.setJsonData(jsonUtil.toJson(bidDto));
        return Optional.of(bidEntity);
    }
}

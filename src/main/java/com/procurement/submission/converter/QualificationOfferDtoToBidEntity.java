package com.procurement.submission.converter;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;

public class QualificationOfferDtoToBidEntity implements Converter<QualificationOfferDto, BidEntity> {
    private final JsonUtil jsonUtil;

    public QualificationOfferDtoToBidEntity(final JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    @Override
    public BidEntity convert(final QualificationOfferDto qualificationOfferDto) {
        final BidEntity bidEntity = new BidEntity();
        bidEntity.setOcId(qualificationOfferDto.getOcid());
        final UUID bidId;
        final BidQualificationDto bidDto = qualificationOfferDto.getBid();
        if (Objects.isNull(qualificationOfferDto.getBid().getId())) {
            bidId = UUIDs.timeBased();
            bidDto.setId(bidId.toString());
        } else {
            bidId = UUID.fromString(bidDto.getId());
        }
        if (Objects.isNull(bidDto.getDate())) {
            bidDto.setDate(LocalDateTime.now());
        }
        bidEntity.setStatus(bidDto.getStatus().value());
        bidEntity.setBidId(bidId);
        bidEntity.setJsonData(jsonUtil.toJson(bidDto));
        return bidEntity;
    }
}

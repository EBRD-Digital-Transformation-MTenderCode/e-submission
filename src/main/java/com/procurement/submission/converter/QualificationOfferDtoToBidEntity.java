package com.procurement.submission.converter;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;

public class QualificationOfferDtoToBidEntity implements Converter<QualificationOfferDto, BidEntity> {
    @Override
    public BidEntity convert(final QualificationOfferDto qualificationOfferDto) {
        final BidEntity bidEntity = new BidEntity();
        bidEntity.setOcId(qualificationOfferDto.getOcid());
        final BidQualificationDto bidDto = qualificationOfferDto.getBid();
        bidEntity.setBidId(getUuid(bidDto));
        if (Objects.isNull(bidDto.getDate())) {
            bidDto.setDate(LocalDateTime.now());
        }
        bidEntity.setStatus(bidDto.getStatus());
        return bidEntity;
    }

    private UUID getUuid(final BidQualificationDto bidDto) {
        final UUID bidId;
        if (Objects.isNull(bidDto.getId())) {
            bidId = UUIDs.timeBased();
            bidDto.setId(bidId.toString());
        } else {
            bidId = UUID.fromString(bidDto.getId());
        }
        return bidId;
    }
}

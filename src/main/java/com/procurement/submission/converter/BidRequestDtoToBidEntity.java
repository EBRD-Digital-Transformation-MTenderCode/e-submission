package com.procurement.submission.converter;

import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.entity.BidEntity;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;

public class BidRequestDtoToBidEntity implements Converter<BidRequestDto, BidEntity> {

    @Override
    public BidEntity convert(final BidRequestDto bidDto) {
        final BidEntity bidEntity = new BidEntity();
        bidEntity.setOcId(bidDto.getOcid());
        bidEntity.setStage(bidDto.getStage());
        bidEntity.setBidId(UUID.fromString(bidDto.getBid().getId()));
        bidEntity.setStatus(bidDto.getBid().getStatus());
        return bidEntity;
    }
}
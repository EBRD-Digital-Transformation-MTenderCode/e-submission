package com.procurement.submission.converter;

import com.datastax.driver.core.utils.UUIDs;
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
        bidEntity.setBidToken(getUuid(bidDto));
        bidEntity.setStatus(bidDto.getBid().getStatus());
        bidEntity.setOwner(bidDto.getOwner());
        return bidEntity;
    }

    private UUID getUuid(final BidRequestDto bidDto) {
        return bidDto.getBidToken() == null ?
               UUIDs.timeBased() :
               UUID.fromString(bidDto.getBidToken());
    }
}

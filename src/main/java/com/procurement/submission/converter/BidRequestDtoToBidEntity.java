package com.procurement.submission.converter;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.model.dto.request.BidRequestDto;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.ocds.Bid;
import java.util.Objects;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;

public class BidRequestDtoToBidEntity implements Converter<BidRequestDto, BidEntity> {

    @Override
    public BidEntity convert(final BidRequestDto bidDto) {
        final BidEntity bidEntity = new BidEntity();
        bidEntity.setOcId(bidDto.getOcid());
        bidEntity.setStage(bidDto.getStage());
        final Bid bid = bidDto.getBid();
        bidEntity.setBidId(UUID.fromString(bid.getId()));


    }

}

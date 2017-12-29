package com.procurement.submission.converter;

import com.procurement.submission.model.dto.response.BidResponse;
import com.procurement.submission.model.entity.BidEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

public class BidEntityToBidResponse implements Converter<BidEntity, BidResponse> {
    @Nullable
    @Override
    public BidResponse convert(final BidEntity bidEntity) {
        BidResponse bidResponse = new BidResponse();
        bidResponse.setOcid(bidEntity.getOcId());
        bidResponse.setStage(bidEntity.getStage());
        bidResponse.setBidSignId(bidEntity.getBidSignId().toString());
        return bidResponse;
    }
}

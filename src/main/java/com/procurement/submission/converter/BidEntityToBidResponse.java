package com.procurement.submission.converter;

import com.procurement.submission.model.dto.response.CommonBidResponse;
import com.procurement.submission.model.entity.BidEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

public class BidEntityToBidResponse implements Converter<BidEntity, CommonBidResponse> {
    @Nullable
    @Override
    public CommonBidResponse convert(final BidEntity bidEntity) {
        CommonBidResponse commonBidResponse = new CommonBidResponse();
        commonBidResponse.setOcid(bidEntity.getOcId());
        commonBidResponse.setStage(bidEntity.getStage());
        commonBidResponse.setBidToken(bidEntity.getBidToken().toString());
        commonBidResponse.setOwner(bidEntity.getOwner());
        return commonBidResponse;
    }
}

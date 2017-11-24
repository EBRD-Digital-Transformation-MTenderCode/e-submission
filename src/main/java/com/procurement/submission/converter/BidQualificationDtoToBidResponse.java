package com.procurement.submission.converter;

import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.OrganizationReferenceDto;
import com.procurement.submission.model.dto.response.BidResponse;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

import static java.util.stream.Collectors.toList;

public class BidQualificationDtoToBidResponse implements Converter<BidQualificationDto, BidResponse> {
    @Override
    public BidResponse convert(final BidQualificationDto source) {
        final List<BidResponse.Tenderer> tenderers = convertTenderers(source.getTenderers());
        final List<BidResponse.RelatedLot> relatedLots = convertRelatedLots(source.getRelatedLots());
        return new BidResponse(source.getId(), tenderers, relatedLots);
    }

    private List<BidResponse.Tenderer> convertTenderers(final List<OrganizationReferenceDto> source) {
        return source.stream()
                     .map(t -> new BidResponse.Tenderer(t.getIdentifier().getId(), t.getIdentifier().getScheme()))
                     .collect(toList());
    }

    private List<BidResponse.RelatedLot> convertRelatedLots(final List<String> relatedLots) {
        return relatedLots.stream()
                          .map(BidResponse.RelatedLot::new)
                          .collect(toList());
    }
}

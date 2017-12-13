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
        return new BidResponse(
            source.getId(), source.getDate(), source.getStatus(), tenderers, source.getRelatedLots());
    }

    private List<BidResponse.Tenderer> convertTenderers(final List<OrganizationReferenceDto> source) {
        return source.stream()
                     .map(t -> new BidResponse.Tenderer(t.getIdentifier().getId(), t.getName(),
                         t.getIdentifier().getScheme()))
                     .collect(toList());
    }
}

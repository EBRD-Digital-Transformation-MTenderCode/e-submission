package com.procurement.submission.converter;

import com.procurement.submission.model.dto.response.BidUpdate;
import com.procurement.submission.model.dto.response.OrganizationReferenceRs;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

import static java.util.stream.Collectors.toList;

public class BidToBidUpdate implements Converter<Bid, BidUpdate> {
    @Override
    public BidUpdate convert(final Bid bid) {
        return new BidUpdate(bid.getId(),
                bid.getDate(),
                bid.getStatus(),
                bid.getStatusDetails(),
                createTenderers(bid.getTenderers()),
                bid.getValue(),
                bid.getDocuments(),
                bid.getRelatedLots());
    }

    // TODO: 11.01.18 refactor - create converter from this
    private List<OrganizationReferenceRs> createTenderers(final List<OrganizationReference> tenderers) {
        return tenderers.stream()
                .map(t -> new OrganizationReferenceRs(t.getId(), t.getName()))
                .collect(toList());
    }
}

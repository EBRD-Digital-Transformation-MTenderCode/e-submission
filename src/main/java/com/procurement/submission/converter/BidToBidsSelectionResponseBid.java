package com.procurement.submission.converter;

import com.procurement.submission.model.dto.response.BidsSelectionResponse;
import com.procurement.submission.model.dto.response.OrganizationReferenceRs;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

import static java.util.stream.Collectors.toList;

public class BidToBidsSelectionResponseBid implements Converter<Bid, BidsSelectionResponse.Bid> {

    @Override
    public BidsSelectionResponse.Bid convert(final Bid bid) {
        final BidsSelectionResponse.Bid newBid = new BidsSelectionResponse.Bid();
        newBid.setId(bid.getId());
        newBid.setDate(bid.getDate());
        newBid.setRelatedLots(bid.getRelatedLots());
        newBid.setValue(bid.getValue());
        newBid.setTenderers(createTenderers(bid.getTenderers()));
        return newBid;
    }

    // TODO: 11.01.18 refactor - create converter from this
    private List<OrganizationReferenceRs> createTenderers(final List<OrganizationReference> tenderers) {
        return tenderers.stream()
                        .map(t -> new OrganizationReferenceRs(t.getId(), t.getName()))
                        .collect(toList());
    }
}

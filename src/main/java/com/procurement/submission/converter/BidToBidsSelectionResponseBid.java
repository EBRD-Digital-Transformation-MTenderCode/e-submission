package com.procurement.submission.converter;

import com.procurement.submission.model.dto.response.BidsSelectionResponse;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

import static java.util.stream.Collectors.toList;

public class BidToBidsSelectionResponseBid implements Converter<Bid, BidsSelectionResponse.Bid> {

    @Override
    public BidsSelectionResponse.Bid convert(final Bid bid) {
        BidsSelectionResponse.Bid newBid = new BidsSelectionResponse.Bid();
        newBid.setId(bid.getId());
        newBid.setRelatedLots(bid.getRelatedLots());
        newBid.setCreateDate(bid.getDate());
        newBid.setValue(bid.getValue());
        newBid.setTenderers(createTenderers(bid.getTenderers()));
        return newBid;
    }

    private List<BidsSelectionResponse.OrganizationReference> createTenderers(
        final List<OrganizationReference> tenderers) {
        return tenderers.stream()
                        .map(t -> new BidsSelectionResponse.OrganizationReference(t.getId(), t.getName()))
                        .collect(toList());
    }
}

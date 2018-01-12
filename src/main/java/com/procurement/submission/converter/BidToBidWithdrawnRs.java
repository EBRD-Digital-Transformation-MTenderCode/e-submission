package com.procurement.submission.converter;

import com.procurement.submission.model.dto.response.BidWithdrawnRs;
import com.procurement.submission.model.dto.response.OrganizationReferenceRs;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.OrganizationReference;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

import static java.util.stream.Collectors.toList;

public class BidToBidWithdrawnRs implements Converter<Bid, BidWithdrawnRs> {
    @Override
    public BidWithdrawnRs convert(final Bid bid) {
        final List<OrganizationReferenceRs> tenderers = createTenderers(bid.getTenderers());
        return new BidWithdrawnRs(bid.getId(), bid.getDate(), bid.getStatus(), tenderers, bid.getValue(),
            bid.getDocuments(), bid.getRelatedLots());
    }

    // TODO: 11.01.18 refactor - create converter from this
    private List<OrganizationReferenceRs> createTenderers(final List<OrganizationReference> tenderers) {
        return tenderers.stream()
                        .map(t -> new OrganizationReferenceRs(t.getId(), t.getName()))
                        .collect(toList());
    }
}

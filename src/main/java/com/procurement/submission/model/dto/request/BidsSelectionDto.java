package com.procurement.submission.model.dto.request;

import com.procurement.submission.model.ocds.Bid;
import lombok.Getter;

@Getter
public class BidsSelectionDto {
    private String ocId;
    private String country;
    private String pmd;
    private String stage;
    private Bid.Status status;

    public BidsSelectionDto(final String ocId,
                            final String country,
                            final String pmd,
                            final String stage,
                            final Bid.Status status) {
        this.ocId = ocId;
        this.country = country;
        this.pmd = pmd;
        this.stage = stage;
        this.status = status;
    }
}

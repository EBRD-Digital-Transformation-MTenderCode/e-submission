package com.procurement.submission.model.dto.request;

import java.util.List;
import javax.validation.Valid;
import lombok.Getter;

@Getter
public class BidAqpDto {

    private String id;
    private BidStatus status;
    @Valid
    private List<DocumentDto> documents;

    public BidAqpDto(final String id,
                     final BidStatus status,
                     final List<DocumentDto> documents) {
        this.id = id;
        this.status = status;
        this.documents = documents;
    }
}


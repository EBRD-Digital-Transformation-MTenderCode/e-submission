package com.procurement.submission.model.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BidsUpdateByLotsDto {
    private String ocid;
    private String stage;
    private String country;
    private String pmd;
    private LotsDto lots;
}

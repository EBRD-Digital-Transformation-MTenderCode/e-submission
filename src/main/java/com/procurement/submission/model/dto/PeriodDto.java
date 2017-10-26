package com.procurement.submission.model.dto;

import lombok.Data;

@Data
public class PeriodDto {
    private String startDate;
    private String endDate;
    private String maxExtentDate;
    private Integer durationInDays;
}
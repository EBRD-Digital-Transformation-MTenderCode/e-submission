package com.procurement.submission.converter;

import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import org.springframework.core.convert.converter.Converter;

public class PeriodDataDtoToPeriodEntity implements Converter<PeriodDataDto, SubmissionPeriodEntity> {

    @Override
    public SubmissionPeriodEntity convert(final PeriodDataDto dataDto) {
        SubmissionPeriodEntity submissionPeriodEntity = new SubmissionPeriodEntity();
        submissionPeriodEntity.setOcId(dataDto.getOcId());
        submissionPeriodEntity.setStartDate(dataDto.getTenderPeriod()
                                                   .getStartDate());
        submissionPeriodEntity.setEndDate(dataDto.getTenderPeriod()
                                                 .getEndDate());

        return submissionPeriodEntity;
    }
}

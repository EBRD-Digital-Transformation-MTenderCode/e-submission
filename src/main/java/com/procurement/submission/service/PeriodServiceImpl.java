package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import java.time.LocalDateTime;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PeriodServiceImpl implements PeriodService {

    private PeriodRepository periodRepository;
    private RulesService rulesService;
    private ConversionService conversionService;

    public PeriodServiceImpl(PeriodRepository periodRepository,
                             RulesService rulesService,
                             ConversionService conversionService) {
        this.periodRepository = periodRepository;
        this.rulesService = rulesService;
        this.conversionService = conversionService;
    }

    @Override
    public Boolean checkPeriod(PeriodDataDto dataDto) {
        Long interval = rulesService.getInterval(dataDto);
        Boolean isValid = false;
        if (interval != 0L) {
            isValid = checkInterval(dataDto.getTenderPeriod()
                                           .getStartDate(),
                                    dataDto.getTenderPeriod()
                                           .getEndDate(),
                                    interval);
        }
        return isValid;
    }

    private Boolean checkInterval(LocalDateTime startDate, LocalDateTime endDate, Long interval) {
        Long days = DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        return (days >= interval);
    }

    @Override
    public SubmissionPeriodEntity savePeriod(PeriodDataDto dataDto) {
        SubmissionPeriodEntity period = conversionService.convert(dataDto, SubmissionPeriodEntity.class);
        return periodRepository.save(period);
    }
}

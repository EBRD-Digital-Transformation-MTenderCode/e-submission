package com.procurement.submission.service;

import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.dto.request.TenderPeriodDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import com.procurement.submission.repository.RulesRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PeriodServiceImpl implements PeriodService {

    private PeriodRepository periodRepository;
    private RulesRepository rulesRepository;

    public PeriodServiceImpl(PeriodRepository periodRepository,
                             RulesRepository rulesRepository) {
        this.periodRepository = periodRepository;
        this.rulesRepository = rulesRepository;
    }

    @Override
    public Boolean checkPeriod(PeriodDataDto dataDto) {
        Objects.requireNonNull(dataDto);
        String value = rulesRepository.getValue(dataDto.getCountry(),
                                                dataDto.getProcurementMethodDetails(),
                                                "interval");
        Long interval = Long.valueOf(value);
        TenderPeriodDto tenderPeriod = dataDto.getTenderPeriod();
        Boolean isValid = false;
        if (Objects.nonNull(tenderPeriod) && interval != 0) {
            isValid = checkInterval(tenderPeriod.getStartDate(), tenderPeriod.getEndDate(), interval);
        }
        return isValid;
    }

    private Boolean checkInterval(LocalDateTime startDate, LocalDateTime endDate, Long interval) {
        Long days = DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        return (days >= interval);
    }

    @Override
    public SubmissionPeriodEntity savePeriod(PeriodDataDto dataDto) {
        Objects.requireNonNull(dataDto);
        Optional<SubmissionPeriodEntity> period = convertDtoToEntity(dataDto.getOcId(), dataDto.getTenderPeriod());
        return period.map(p -> periodRepository.save(p))
                     .orElse(null);
    }

    public Optional<SubmissionPeriodEntity> convertDtoToEntity(String ocId, TenderPeriodDto periodDto) {
        Objects.requireNonNull(ocId);
        Objects.requireNonNull(periodDto);
        SubmissionPeriodEntity submissionPeriodEntity = new SubmissionPeriodEntity();
        submissionPeriodEntity.setOcId(ocId);
        submissionPeriodEntity.setStartDate(periodDto.getStartDate());
        submissionPeriodEntity.setEndDate(periodDto.getEndDate());
        return Optional.of(submissionPeriodEntity);
    }
}

package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.response.CheckPeriodResponse;
import com.procurement.submission.model.dto.response.SavePeriodResponse;
import com.procurement.submission.model.entity.PeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import com.procurement.submission.utils.DateUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PeriodServiceImpl implements PeriodService {

    private final PeriodRepository periodRepository;
    private final RulesService rulesService;
    private final DateUtil dateUtil;


    public PeriodServiceImpl(final PeriodRepository periodRepository,
                             final RulesService rulesService,
                             final DateUtil dateUtil) {
        this.periodRepository = periodRepository;
        this.rulesService = rulesService;
        this.dateUtil = dateUtil;
    }

    @Override
    public ResponseDto checkPeriod(final String country,
                                   final String pmd,
                                   final String stage,
                                   final LocalDateTime startDate,
                                   final LocalDateTime endDate) {
        final int interval = rulesService.getInterval(country, pmd);
        final Boolean isValid = checkInterval(startDate, endDate, interval);
        return getResponseDto(new CheckPeriodResponse(isValid));
    }

    @Override
    public void checkPeriod(final String cpid) {
        if (isPeriodValid(cpid)) {
            throw new ErrorException("Not found date.");
        }
    }

    @Override
    public ResponseDto savePeriod(final String cpid,
                                  final String stage,
                                  final LocalDateTime startDate,
                                  final LocalDateTime endDate) {
        final PeriodEntity period = new PeriodEntity();
        period.setCpId(cpid);
        period.setStage(stage);
        period.setStartDate(dateUtil.localToDate(startDate));
        period.setEndDate(dateUtil.localToDate(endDate));
        periodRepository.save(period);
        return getResponseDto(new SavePeriodResponse(
                period.getCpId(),
                period.getStartDate(),
                period.getEndDate()));
    }

    @Override
    public PeriodEntity getPeriod(final String cpid) {
        final Optional<PeriodEntity> entityOptional = periodRepository.getByCpId(cpid);
        if (entityOptional.isPresent()) {
            return entityOptional.get();
        } else {
            throw new ErrorException("Period not found");
        }
    }

    @Override
    public boolean isPeriodValid(final String cpid) {
        final LocalDateTime localDateTime = dateUtil.localNowUTC();
        final PeriodEntity periodEntity = getPeriod(cpid);
        final boolean localDateTimeAfter = localDateTime.isAfter(periodEntity.getStartDate());
        final boolean localDateTimeBefore = localDateTime.isBefore(periodEntity.getEndDate());
        return !localDateTimeAfter || !localDateTimeBefore;
    }

    private Boolean checkInterval(final LocalDateTime startDate, final LocalDateTime endDate, final int interval) {
        final long days = DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        return days >= interval;
    }

    private ResponseDto getResponseDto(final Object data) {
        return new ResponseDto(true, null, data);
    }

}

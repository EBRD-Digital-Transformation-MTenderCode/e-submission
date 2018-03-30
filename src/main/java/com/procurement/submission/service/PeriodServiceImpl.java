package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.exception.ErrorType;
import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.response.CheckPeriodResponseDto;
import com.procurement.submission.model.ocds.Period;
import com.procurement.submission.model.entity.PeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import com.procurement.submission.utils.DateUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class PeriodServiceImpl implements PeriodService {
    private static final String TEST_PARAM = "TEST";
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
    public ResponseDto savePeriod(final String cpId,
                                  final String stage,
                                  final LocalDateTime startDate,
                                  final LocalDateTime endDate) {
        final PeriodEntity period = new PeriodEntity();
        period.setCpId(cpId);
        period.setStage(stage);
        period.setStartDate(dateUtil.localToDate(startDate));
        period.setEndDate(dateUtil.localToDate(endDate));
        periodRepository.save(period);
        return new ResponseDto<>(true, null, new Period(period.getStartDate(), period.getEndDate()));
    }

    @Override
    public ResponseDto saveNewPeriod(final String cpId,
                                     final String stage,
                                     final String country,
                                     final String pmd,
                                     final LocalDateTime startDate) {
        final PeriodEntity period = new PeriodEntity();
        period.setCpId(cpId);
        period.setStage(stage);
        period.setStartDate(dateUtil.localToDate(startDate));
        final int interval = rulesService.getInterval(country, pmd);
        period.setEndDate(dateUtil.localToDate(startDate.plusDays(interval)));
        periodRepository.save(period);
        return new ResponseDto<>(true, null, new Period(period.getStartDate(), period.getEndDate()));
    }

    @Override
    public void checkCurrentDateInPeriod(final String cpId, final String stage) {
        if (!isPeriodValid(cpId, stage)) {
            throw new ErrorException(ErrorType.INVALID_DATE);
        }
    }

    @Override
    public void checkIsPeriodExpired(final String cpId, final String stage) {
        if (!isPeriodValid(cpId, stage)) {
            throw new ErrorException(ErrorType.PERIOD_NOT_EXPIRED);
        }
    }

    @Override
    public PeriodEntity getPeriod(final String cpId, final String stage) {
        final Optional<PeriodEntity> entityOptional = periodRepository.getByCpIdAndStage(cpId, stage);
        if (!entityOptional.isPresent()) throw new ErrorException(ErrorType.PERIOD_NOT_FOUND);
        return entityOptional.get();
    }

    @Override
    public ResponseDto checkPeriod(final String cpId,
                                   final String country,
                                   final String pmd,
                                   final String stage,
                                   final LocalDateTime startDate,
                                   final LocalDateTime endDate) {
        return new ResponseDto<>(true, null,
                new CheckPeriodResponseDto(
                        checkInterval(country, pmd, startDate, endDate),
                        isPeriodChange(cpId, stage, startDate, endDate)));
    }

    @Override
    public ResponseDto periodValidation(final String country,
                                        final String pmd,
                                        final LocalDateTime startDate,
                                        final LocalDateTime endDate) {
        if (checkInterval(country, pmd, startDate, endDate))
            throw new ErrorException(ErrorType.INVALID_PERIOD);
        return new ResponseDto<>(true, null, "Period is valid.");
    }

    public boolean isPeriodValid(final String cpId, final String stage) {
        final LocalDateTime localDateTime = dateUtil.localNowUTC();
        final PeriodEntity periodEntity = getPeriod(cpId, stage);
        final boolean localDateTimeAfter = localDateTime.isAfter(periodEntity.getStartDate())
                && localDateTime.isEqual(periodEntity.getStartDate());
        final boolean localDateTimeBefore = localDateTime.isBefore(periodEntity.getEndDate())
                && localDateTime.isEqual(periodEntity.getEndDate());
        return localDateTimeAfter && localDateTimeBefore;
    }

    public boolean isPeriodChange(final String cpId,
                                  final String stage,
                                  final LocalDateTime startDate,
                                  final LocalDateTime endDate) {
        final PeriodEntity period = getPeriod(cpId, stage);
        return (period.getStartDate() == startDate) && (period.getEndDate() == endDate);
    }

    private Boolean checkInterval(final String country,
                                  final String pmd,
                                  final LocalDateTime startDate,
                                  final LocalDateTime endDate) {
        final int interval = rulesService.getInterval(country, pmd);
        if (TEST_PARAM.equals(country) && TEST_PARAM.equals(pmd)) {
            final long minutes = MINUTES.between(startDate, endDate);
            return minutes >= interval;
        }
        final long days = DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        return days >= interval;
    }
}

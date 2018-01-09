package com.procurement.submission.service;

import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.bpe.ResponseDto;
import com.procurement.submission.model.dto.response.CheckPeriodResponse;
import com.procurement.submission.model.dto.response.SavePeriodResponse;
import com.procurement.submission.model.entity.PeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import com.procurement.submission.utils.DateUtil;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PeriodServiceImpl implements PeriodService {

    private final PeriodRepository periodRepository;
    private final RulesService rulesService;
    private final ConversionService conversionService;
    private final DateUtil dateUtil;


    public PeriodServiceImpl(final PeriodRepository periodRepository,
                             final RulesService rulesService,
                             final ConversionService conversionService,
                             final DateUtil dateUtil) {
        this.periodRepository = periodRepository;
        this.rulesService = rulesService;
        this.conversionService = conversionService;
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
        return new ResponseDto(true, null, new CheckPeriodResponse(isValid));
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
        return new ResponseDto(
                true,
                null,
                new SavePeriodResponse(period.getCpId(),
                        dateUtil.dateToLocal(period.getStartDate()),
                        dateUtil.dateToLocal(period.getEndDate())));
    }


    @Override
    public void checkPeriod(final String cpid) {
        final LocalDateTime localDateTime = dateUtil.localNowUTC();
        final Optional<PeriodEntity> entityOptional = periodRepository.getByCpId(cpid);
        if (entityOptional.isPresent()) {
            final PeriodEntity periodEntity = entityOptional.get();
            final boolean localDateTimeAfter = localDateTime.isAfter(dateUtil.dateToLocal(periodEntity.getStartDate()));
            final boolean localDateTimeBefore = localDateTime.isBefore(dateUtil.dateToLocal(periodEntity.getEndDate()));
            if (!localDateTimeAfter || !localDateTimeBefore) {
                throw new ErrorException("Date does not match period.");
            }
        } else {
            throw new ErrorException("Period not found");
        }
    }

    private Boolean checkInterval(final LocalDateTime startDate, final LocalDateTime endDate, final int interval) {
        final long days = DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        return days >= interval;
    }
}

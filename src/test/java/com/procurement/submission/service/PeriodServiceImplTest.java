package com.procurement.submission.service;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PeriodServiceImplTest {

    private static PeriodService periodService;

    private static PeriodDataDto periodDataDto;
    private static SubmissionPeriodEntity submissionPeriodEntity;

    @BeforeAll
    static void setUp() {
        JsonUtil jsonUtil = new JsonUtil();

        PeriodRepository periodRepository = mock(PeriodRepository.class);
        RulesService rulesService = mock(RulesService.class);
        ConversionService conversionService = mock(ConversionService.class);

        String json = jsonUtil.getResource("json/period-data.json");
        periodDataDto = jsonUtil.toObject(PeriodDataDto.class, json);

        submissionPeriodEntity = new SubmissionPeriodEntity();
        submissionPeriodEntity.setOcId(periodDataDto.getOcId());
        submissionPeriodEntity.setStartDate(periodDataDto.getTenderPeriod()
                                                         .getStartDate());
        submissionPeriodEntity.setEndDate(periodDataDto.getTenderPeriod()
                                                       .getEndDate());

        when(rulesService.getInterval(periodDataDto)).thenReturn(15L);

        when(periodRepository.save(submissionPeriodEntity)).thenReturn(submissionPeriodEntity);

        when(conversionService.convert(periodDataDto, SubmissionPeriodEntity.class)).thenReturn(submissionPeriodEntity);

        periodService = new PeriodServiceImpl(periodRepository, rulesService, conversionService);
    }

    @Test
    void checkPeriod() {
        Boolean isValid = periodService.checkPeriod(periodDataDto);
        assertTrue(isValid);
    }

    @Test
    void savePeriod() {
        SubmissionPeriodEntity result = periodService.savePeriod(periodDataDto);
        assertEquals(result.getOcId(), submissionPeriodEntity.getOcId());
    }
}
package com.procurement.submission.service;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.dto.request.TenderPeriodDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import com.procurement.submission.repository.PeriodRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PeriodServiceImplTest {

    private static PeriodService periodService;

    private static PeriodDataDto periodDataDto;
    private static PeriodDataDto periodDataDtoForNotEqualDays;
    private static PeriodDataDto periodDataDtoForEqualDays;
    private static PeriodDataDto periodDataDtoForFalse;
    private static SubmissionPeriodEntity submissionPeriodEntity;

    @BeforeAll
    static void setUp() {
        JsonUtil jsonUtil = new JsonUtil();

        PeriodRepository periodRepository = mock(PeriodRepository.class);
        RulesService rulesService = mock(RulesService.class);
        ConversionService conversionService = mock(ConversionService.class);

        final String json = jsonUtil.getResource("json/period-data.json");
        periodDataDto = jsonUtil.toObject(PeriodDataDto.class, json);
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        final TenderPeriodDto tenderPeriodForEqualDays =
            new TenderPeriodDto(localDateTimeNow, localDateTimeNow.plusDays(5L));
        final TenderPeriodDto tenderPeriodForNotEqualDays =
            new TenderPeriodDto(localDateTimeNow, localDateTimeNow.plusDays(4L));
        periodDataDtoForEqualDays = new PeriodDataDto("ocid", "country", "detail", tenderPeriodForEqualDays);
        periodDataDtoForNotEqualDays = new PeriodDataDto("ocid", "country", "detail", tenderPeriodForNotEqualDays);
        periodDataDtoForFalse = new PeriodDataDto("ocid", "country", "detail", periodDataDto.getTenderPeriod());

        submissionPeriodEntity = new SubmissionPeriodEntity();
        submissionPeriodEntity.setOcId(periodDataDto.getOcId());
        final TenderPeriodDto tenderPeriod = periodDataDto.getTenderPeriod();
        submissionPeriodEntity.setStartDate(tenderPeriod.getStartDate());
        submissionPeriodEntity.setEndDate(tenderPeriod.getEndDate());

        when(rulesService.getInterval(periodDataDto)).thenReturn(15L);
        when(rulesService.getInterval(periodDataDtoForEqualDays)).thenReturn(5L);
        when(rulesService.getInterval(periodDataDtoForNotEqualDays)).thenReturn(5L);
        when(rulesService.getInterval(periodDataDtoForFalse)).thenReturn(0L);
        when(periodRepository.save(submissionPeriodEntity)).thenReturn(submissionPeriodEntity);
        when(periodRepository.getByOcId("validPeriod")).thenReturn(createValidSubmissionPeriodEntity());
        when(periodRepository.getByOcId("afterPeriod")).thenReturn(createAfterSubmissionPeriodEntity());
        when(periodRepository.getByOcId("beforePeriod")).thenReturn(createBeforeSubmissionPeriodEntity());
        when(conversionService.convert(periodDataDto, SubmissionPeriodEntity.class)).thenReturn(submissionPeriodEntity);
        periodService = new PeriodServiceImpl(periodRepository, rulesService, conversionService);
    }

    @Test
    void checkPeriodTrue() {
        Boolean isValid = periodService.checkPeriod(periodDataDto);
        assertTrue(isValid);
    }

    @Test
    void checkPeriodEqualDays() {
        Boolean isValid = periodService.checkPeriod(periodDataDtoForEqualDays);
        assertTrue(isValid);
    }

    @Test
    void checkPeriodNotEqualDays() {
        Boolean isValid = periodService.checkPeriod(periodDataDtoForNotEqualDays);
        assertFalse(isValid);
    }

    @Test
    void checkPeriodFalse() {
        Boolean isValid = periodService.checkPeriod(periodDataDtoForFalse);
        assertFalse(isValid);
    }

    @Test
    void savePeriod() {
        SubmissionPeriodEntity result = periodService.savePeriod(periodDataDto);
        assertEquals(result.getOcId(), submissionPeriodEntity.getOcId());
    }

    @Test
    void testCheckPeriodValid() {
        periodService.checkPeriod("validPeriod");
    }

    @Test
    void testCheckPeriodAfter() {
        ErrorException exception = assertThrows(ErrorException.class,
            () -> periodService.checkPeriod("afterPeriod")
        );
        assertEquals("Not found date.", exception.getMessage());
    }

    @Test
    void testCheckPeriodBefore() {
        ErrorException exception = assertThrows(ErrorException.class,
            () -> periodService.checkPeriod("beforePeriod")
        );
        assertEquals("Not found date.", exception.getMessage());
    }

    private static SubmissionPeriodEntity createValidSubmissionPeriodEntity() {
        SubmissionPeriodEntity submissionPeriodEntity = new SubmissionPeriodEntity();
        LocalDateTime now = LocalDateTime.now();
        submissionPeriodEntity.setStartDate(now.minusDays(2L));
        submissionPeriodEntity.setEndDate(now.plusDays(2L));
        return submissionPeriodEntity;
    }

    private static SubmissionPeriodEntity createAfterSubmissionPeriodEntity() {
        SubmissionPeriodEntity submissionPeriodEntity = new SubmissionPeriodEntity();
        LocalDateTime now = LocalDateTime.now();
        submissionPeriodEntity.setStartDate(now.plusDays(2L));
        submissionPeriodEntity.setEndDate(now.plusDays(4L));
        return submissionPeriodEntity;
    }

    private static SubmissionPeriodEntity createBeforeSubmissionPeriodEntity() {
        SubmissionPeriodEntity submissionPeriodEntity = new SubmissionPeriodEntity();
        LocalDateTime now = LocalDateTime.now();
        submissionPeriodEntity.setStartDate(now.minusDays(4L));
        submissionPeriodEntity.setEndDate(now.minusDays(2L));
        return submissionPeriodEntity;
    }
}
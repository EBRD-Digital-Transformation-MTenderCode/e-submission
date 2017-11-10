package com.procurement.submission.repository;

import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PeriodRepositoryTest {

    private static PeriodRepository periodRepository;

    private static SubmissionPeriodEntity submissionPeriodEntity;

    @BeforeAll
    static void setUp() {
        submissionPeriodEntity = new SubmissionPeriodEntity();
        submissionPeriodEntity.setOcId("ocds-213czf-000-00001");
        submissionPeriodEntity.setStartDate(LocalDateTime.now());
        submissionPeriodEntity.setEndDate(LocalDateTime.now()
                                                       .plusDays(20));
        periodRepository = mock(PeriodRepository.class);
        when(periodRepository.getByOcId(submissionPeriodEntity.getOcId())).thenReturn(submissionPeriodEntity);
        when(periodRepository.save(submissionPeriodEntity)).thenReturn(submissionPeriodEntity);
    }

    @Test
    public void save() {
        SubmissionPeriodEntity result = periodRepository.save(submissionPeriodEntity);
        assertEquals(result.getOcId(), submissionPeriodEntity.getOcId());
    }

    @Test
    public void getByOcId() {
        SubmissionPeriodEntity result = periodRepository.getByOcId(submissionPeriodEntity.getOcId());
        assertEquals(result.getOcId(), submissionPeriodEntity.getOcId());
    }
}
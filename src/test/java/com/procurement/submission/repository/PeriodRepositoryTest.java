package com.procurement.submission.repository;

import com.procurement.submission.model.entity.PeriodEntity;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PeriodRepositoryTest {

//    private static PeriodRepository periodRepository;
//
//    private static PeriodEntity periodEntity;
//
//    @BeforeAll
//    static void setUp() {
//        periodEntity = new PeriodEntity();
//        periodEntity.setOcId("ocds-213czf-000-00001");
//        periodEntity.setStartDate(LocalDateTime.now());
//        periodEntity.setEndDate(LocalDateTime.now()
//                                                       .plusDays(20));
//        periodRepository = mock(PeriodRepository.class);
//        when(periodRepository.getByOcId(periodEntity.getOcId())).thenReturn(periodEntity);
//        when(periodRepository.save(periodEntity)).thenReturn(periodEntity);
//    }
//
//    @Test
//    public void save() {
//        PeriodEntity result = periodRepository.save(periodEntity);
//        assertEquals(result.getOcId(), periodEntity.getOcId());
//    }
//
//    @Test
//    public void getByOcId() {
//        PeriodEntity result = periodRepository.getByOcId(periodEntity.getOcId());
//        assertEquals(result.getOcId(), periodEntity.getOcId());
//    }
}
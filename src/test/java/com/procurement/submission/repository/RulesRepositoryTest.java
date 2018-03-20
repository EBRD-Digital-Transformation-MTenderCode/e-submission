package com.procurement.submission.repository;

import com.procurement.submission.model.entity.RulesEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RulesRepositoryTest {

//    private static RulesRepository rulesRepository;
//
//    private static RulesEntity rulesEntity;
//
//    @BeforeAll
//    static void setUp() {
//        rulesEntity = new RulesEntity();
//        rulesEntity.setCountry("UA");
//        rulesEntity.setMethod("micro");
//        rulesEntity.setParameter("interval");
//        rulesEntity.setValue("15");
//        rulesRepository = mock(RulesRepository.class);
//        when(rulesRepository.getValue(rulesEntity.getCountry(), rulesEntity.getMethod(), rulesEntity.getParameter())
//        ).thenReturn(rulesEntity.getValue());
//    }
//
//
//    @Test
//    public void getValueTest() {
//        String value = rulesRepository.getValue(rulesEntity.getCountry(), rulesEntity.getMethod(), rulesEntity
//                .getParameter());
//        assertEquals(value, rulesEntity.getValue());
//    }
}
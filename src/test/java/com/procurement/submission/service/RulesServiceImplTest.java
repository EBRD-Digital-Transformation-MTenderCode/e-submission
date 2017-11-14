package com.procurement.submission.service;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.repository.RulesRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RulesServiceImplTest {

    private static PeriodDataDto periodDataDto;
    private static RulesService rulesService;

    @BeforeAll
    static void setUp() {
        JsonUtil jsonUtil = new JsonUtil();

        RulesRepository rulesRepository = mock(RulesRepository.class);
        String json = jsonUtil.getResource("json/period-data.json");
        periodDataDto = jsonUtil.toObject(PeriodDataDto.class, json);

        when(rulesRepository.getValue(periodDataDto.getCountry(), periodDataDto.getProcurementMethodDetails(),
                                      "interval")).thenReturn("15");
        rulesService = new RulesServiceImpl(rulesRepository);
    }

    @Test
    public void getInterval() {
        Long result = rulesService.getInterval(periodDataDto);
        assertEquals(result.longValue(), 15L);
    }
}
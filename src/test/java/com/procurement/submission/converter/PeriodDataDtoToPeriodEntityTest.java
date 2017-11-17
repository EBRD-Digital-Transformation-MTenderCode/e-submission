package com.procurement.submission.converter;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.model.dto.request.PeriodDataDto;
import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PeriodDataDtoToPeriodEntityTest {

    @Test
    public void testConverter() {
        final PeriodDataDto periodDataDtoExpected = createPeriodDataDto();
        final SubmissionPeriodEntity submissionPeriodEntityActual = new PeriodDataDtoToPeriodEntity().convert
            (periodDataDtoExpected);

        assertAll(
            () -> assertEquals(periodDataDtoExpected.getOcId(), submissionPeriodEntityActual.getOcId()),
            () -> assertEquals(periodDataDtoExpected.getTenderPeriod().getStartDate(),
                submissionPeriodEntityActual.getStartDate()),
            () -> assertEquals(periodDataDtoExpected.getTenderPeriod().getEndDate(),
                submissionPeriodEntityActual.getEndDate())
        );
    }

    private PeriodDataDto createPeriodDataDto() {
        final JsonUtil jsonUtil = new JsonUtil();
        return jsonUtil.toObject(PeriodDataDto.class, jsonUtil.getResource("json/period-data.json"));
    }
}

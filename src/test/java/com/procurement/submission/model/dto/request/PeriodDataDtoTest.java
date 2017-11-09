package com.procurement.submission.model.dto.request;

import com.procurement.submission.AbstractDomainObjectTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class PeriodDataDtoTest extends AbstractDomainObjectTest {

    @Test
    @DisplayName("Testing mapping json to PeriodDataDto and to json")
    public void testJsonToDataDtoToJson() {
        compare(PeriodDataDto.class, "json/period-data.json");
    }

}
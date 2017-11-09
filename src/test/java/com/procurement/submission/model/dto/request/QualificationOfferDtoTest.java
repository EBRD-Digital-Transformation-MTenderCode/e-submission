package com.procurement.submission.model.dto.request;

import com.procurement.submission.AbstractDomainObjectTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QualificationOfferDtoTest extends AbstractDomainObjectTest {

    @Test
    @DisplayName("Testing mapping json to QualificationOfferDto and to json")
    public void testJsonToDataDtoToJson() {
        compare(QualificationOfferDto.class, "json/qualification-offer.json");
    }
}
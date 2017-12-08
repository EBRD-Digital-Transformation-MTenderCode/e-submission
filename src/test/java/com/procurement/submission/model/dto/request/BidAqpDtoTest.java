package com.procurement.submission.model.dto.request;

import com.procurement.submission.AbstractDomainObjectTest;
import org.junit.jupiter.api.Test;

public class BidAqpDtoTest extends AbstractDomainObjectTest {
    @Test
    public void testMappingToBidAqpDto() {
        compare(BidAqpDto.class, "json/BidAqpDto.json");
    }
}

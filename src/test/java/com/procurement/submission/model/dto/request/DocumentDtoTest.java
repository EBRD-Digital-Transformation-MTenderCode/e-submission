package com.procurement.submission.model.dto.request;

import com.procurement.submission.AbstractDomainObjectTest;
import org.junit.jupiter.api.Test;

public class DocumentDtoTest extends AbstractDomainObjectTest {
    @Test
    public void testMappingToDocumentDto() {
        compare(DocumentDto.class, "json/DocumentDto.json");
    }
}

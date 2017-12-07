package com.procurement.submission.converter;

import com.procurement.submission.JsonUtil;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class QualificationOfferDtoToBidEntityTest {

    @Test
    void testConvert() {
        final QualificationOfferDto qualificationOfferDtoExpected = createQualificationOfferDto();
        final BidQualificationDto bidExpected = qualificationOfferDtoExpected.getBid();
        final BidEntity bidEntityActual = new QualificationOfferDtoToBidEntity().convert(qualificationOfferDtoExpected);
        assertAll(
            () -> assertEquals(qualificationOfferDtoExpected.getOcid(), bidEntityActual.getOcId()),
            () -> assertEquals(bidExpected.getId(), bidEntityActual.getBidId().toString()),
            () -> assertEquals(bidExpected.getStatus(), bidEntityActual.getStatus()),
            () -> assertNull(bidEntityActual.getJsonData())
        );
    }

    @Test
    void testConvertWithNullFields() {
        final QualificationOfferDto qualificationOfferDtoExpected = createQualificationOfferDto();
        qualificationOfferDtoExpected.getBid().setDate(null);
        qualificationOfferDtoExpected.getBid().setId(null);
        final BidQualificationDto bidExpected = qualificationOfferDtoExpected.getBid();
        assertAll(
            () -> assertNull(bidExpected.getId()),
            () -> assertNull(bidExpected.getDate())
        );
        final BidEntity bidEntityActual = new QualificationOfferDtoToBidEntity().convert(qualificationOfferDtoExpected);
        assertAll(
            () -> assertEquals(qualificationOfferDtoExpected.getOcid(), bidEntityActual.getOcId()),
            () -> assertNotNull(bidExpected.getId()),
            () -> assertNotNull(bidExpected.getDate()),
            () -> assertEquals(bidExpected.getStatus(), bidEntityActual.getStatus()),
            () -> assertNull(bidEntityActual.getJsonData())
        );
    }

    private QualificationOfferDto createQualificationOfferDto() {
        final JsonUtil jsonUtil = new JsonUtil();
        final String qualificationOfferStr = jsonUtil.getResource("json/qualification-offer.json");
        return jsonUtil.toObject(QualificationOfferDto.class, qualificationOfferStr);
    }
}

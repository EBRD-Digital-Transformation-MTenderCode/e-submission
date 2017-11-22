package com.procurement.submission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.submission.model.dto.request.BidQualificationDto;
import com.procurement.submission.model.dto.request.BidStatus;
import com.procurement.submission.model.dto.request.QualificationOfferDto;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BidServiceTest {

    private PeriodService periodService;
    private BidRepository bidRepository;
    private ConversionService conversionService;
    private BidService bidService;

    private static QualificationOfferDto qualificationOfferDto = createQualificationOfferDto();

    @BeforeEach
    void initEach() {
        periodService = mock(PeriodService.class);
        bidRepository = mock(BidRepository.class);
        conversionService = mock(ConversionService.class);
        bidService = new BidServiceImpl(periodService, bidRepository, conversionService,
            new JsonUtil(new ObjectMapper()));
    }

    @Test
    @DisplayName("Test verifying invocations without null after converting.")
    void testInsertDataValid() {
        when(conversionService.convert(qualificationOfferDto, BidEntity.class)).thenReturn(new BidEntity());
        when(bidRepository.save(new BidEntity())).thenReturn(new BidEntity());
        bidService.insertData(qualificationOfferDto);
        verify(periodService, times(1)).checkPeriod("ocid");
        verify(conversionService, times(1)).convert(qualificationOfferDto, BidEntity.class);
        verify(bidRepository, times(1)).save(any(BidEntity.class));
    }

    @Test
    @DisplayName("Test verifying invocations with null after converting.")
    void testInsertData() {
        when(conversionService.convert(qualificationOfferDto, BidEntity.class)).thenReturn(null);
        when(bidRepository.save(new BidEntity())).thenReturn(new BidEntity());
        bidService.insertData(qualificationOfferDto);
        verify(periodService, times(1)).checkPeriod("ocid");
        verify(conversionService, times(1)).convert(qualificationOfferDto, BidEntity.class);
        verify(bidRepository, never()).save(any(BidEntity.class));
    }

    private static QualificationOfferDto createQualificationOfferDto() {
        final BidQualificationDto bidQualificationDto = new BidQualificationDto("id", LocalDateTime.now(),
            BidStatus.PENDING, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        return new QualificationOfferDto("ocid", bidQualificationDto);
    }
}

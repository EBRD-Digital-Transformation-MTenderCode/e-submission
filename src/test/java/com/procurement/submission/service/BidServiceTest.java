package com.procurement.submission.service;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.submission.exception.ErrorException;
import com.procurement.submission.model.ocds.Address;
import com.procurement.submission.model.ocds.Bid;
import com.procurement.submission.model.ocds.BidStatus;
import com.procurement.submission.model.ocds.ContactPoint;
import com.procurement.submission.model.ocds.Identifier;
import com.procurement.submission.model.ocds.OrganizationReference;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.repository.BidRepository;
import com.procurement.submission.utils.JsonUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private RulesService rulesService;

    private static JsonUtil jsonUtil = new JsonUtil(new ObjectMapper());

    @BeforeEach
    void initEach() {
        periodService = mock(PeriodService.class);
        bidRepository = mock(BidRepository.class);
        conversionService = mock(ConversionService.class);
        rulesService = mock(RulesService.class);
        bidService = new BidServiceImpl(periodService, bidRepository, conversionService,
            jsonUtil, rulesService);
    }
/*
    @Test
    @DisplayName("Test verifying invocations without null after converting.")
    void testInsertDataValid() throws URISyntaxException {
        QualificationOfferDto qualificationOfferDto = createQualificationOfferDto();
        when(conversionService.convert(qualificationOfferDto, BidEntity.class)).thenReturn(new BidEntity());
        when(bidRepository.save(new BidEntity())).thenReturn(new BidEntity());
        bidService.insertData(qualificationOfferDto);
        verify(periodService, times(1)).checkPeriod("ocid");
        verify(conversionService, times(1)).convert(qualificationOfferDto, BidEntity.class);
        verify(bidRepository, times(1)).save(any(BidEntity.class));
    }

    @Test
    @DisplayName("Test verifying invocations with null after converting.")
    void testInsertData() throws URISyntaxException {
        QualificationOfferDto qualificationOfferDto = createQualificationOfferDto();
        when(conversionService.convert(qualificationOfferDto, BidEntity.class)).thenReturn(null);
        when(bidRepository.save(new BidEntity())).thenReturn(new BidEntity());
        bidService.insertData(qualificationOfferDto);
        verify(periodService, times(1)).checkPeriod("ocid");
        verify(conversionService, times(1)).convert(qualificationOfferDto, BidEntity.class);
        verify(bidRepository, never()).save(any(BidEntity.class));
    }

    @Test
    @DisplayName("Test .getBids in successful scenario")
    void testGetBids() throws URISyntaxException {
        final List<BidEntity> bidEntities = createBids();
        when(bidRepository.findAllByOcIdAndStage("ocid", "stage"))
            .thenReturn(bidEntities);
        when(rulesService.getRulesMinBids("UA", "method"))
            .thenReturn(2);
        final LocalDateTime dateTime = LocalDateTime.now();
        final BidResponseOld bidResponseOld = new BidResponseOld("id", dateTime, BidStatus.INVITED,
                                                                 Collections.singletonList(new BidResponseOld.Tenderer("id", "name", "scheme")),
                                                                 Collections.singletonList("str"));
        when(conversionService.convert(any(Bid.class), eq(BidResponseOld.class)))
            .thenReturn(bidResponseOld);
        final BidsResponse bids = bidService.getBids(new BidsParamDto("ocid", "method", "stage", "UA"));
        final List<BidResponseOld> bidsList = bids.getBids();
        assertEquals(3, bidsList.size());
        bidsList.stream()
                .peek(b -> assertTrue(b.getBidId().equals("id")))
                .peek(b -> b.getTenderers()
                            .forEach(tenderer -> assertTrue(tenderer.getId().equals("id") &
                                tenderer.getScheme().equals("scheme"))))
                .forEach(b -> b.getRelatedLots()
                               .forEach(relatedLots -> assertTrue(relatedLots.equals("str"))));
        verify(bidRepository, times(1)).findAllByOcIdAndStage("ocid", "stage");
        verify(rulesService, times(1)).getRulesMinBids("UA", "method");
        verify(conversionService, times(3)).convert(any(Bid.class), eq(BidResponseOld.class));
    }

    @Test
    @DisplayName("Throw exception in method .getBids")
    void testGetBidsError() throws URISyntaxException {
        final List<BidEntity> bidEntities = createBids();
        when(bidRepository.findAllByOcIdAndStage("ocid", "stage"))
            .thenReturn(bidEntities);
        when(rulesService.getRulesMinBids("UA", "method"))
            .thenReturn(4);
        assertThrows(ErrorException.class,
            () -> bidService.getBids(new BidsParamDto("ocid", "method", "stage", "UA")),
            "Insufficient number of unique bids");
        verify(bidRepository, times(1)).findAllByOcIdAndStage("ocid", "stage");
        verify(rulesService, times(1)).getRulesMinBids("UA", "method");
        verify(conversionService, never()).convert(any(Bid.class), eq(BidResponseOld.class));
    }

    private List<BidEntity> createBids() throws URISyntaxException {
        UUID id1 = UUIDs.random();
        UUID id2 = UUIDs.random();
        UUID id3 = UUIDs.random();
        Bid bid1 = createBidQualificationDto(id1.toString(), "str1", "str1");
        Bid bid2 = createBidQualificationDto(id2.toString(), "str2", "str2");
        Bid bid3 = createBidQualificationDto(id3.toString(), "str3", "str3");
        BidEntity bidEntity1 = createBibEntity(id1, "ocid1", BidStatus.INVITED, "stage1", bid1);
        BidEntity bidEntity2 = createBibEntity(id2, "ocid2", BidStatus.INVITED, "stage2", bid2);
        BidEntity bidEntity3 = createBibEntity(id3, "ocid3", BidStatus.INVITED, "stage3", bid3);
        List<BidEntity> bidEntities = new ArrayList<>();
        bidEntities.add(bidEntity1);
        bidEntities.add(bidEntity2);
        bidEntities.add(bidEntity3);
        return bidEntities;
    }

    private BidEntity createBibEntity(final UUID id1, final String ocid, final BidStatus status, final String stage,
                                      final Bid bid) {
        BidEntity bidEntity = new BidEntity();
        bidEntity.setBidId(id1);
        bidEntity.setOcId(ocid);
        bidEntity.setStatus(status);
        bidEntity.setStage(stage);
        bidEntity.setJsonData(jsonUtil.toJson(bid));
        return bidEntity;
    }*/

/*    private QualificationOfferDto createQualificationOfferDto() throws URISyntaxException {
        final Bid bidQualificationDto =
            createBidQualificationDto(UUID.randomUUID().toString(), "str1", "str1");
        return new QualificationOfferDto("ocid", "stage", bidQualificationDto);
    }

    private Bid createBidQualificationDto(
        final String id1, final String nameOrganizationReferenceDt, final String schemeOrganizationReferenceDt)
        throws URISyntaxException {
        return new Bid(id1, LocalDateTime.now(), BidStatus.PENDING,
                       Collections.singletonList(
                createOrganizationReferenceDto(nameOrganizationReferenceDt, schemeOrganizationReferenceDt)),
                       new ArrayList<>(), Collections.singletonList("str")
        );
    }*/

    private OrganizationReference createOrganizationReferenceDto(final String name, final String scheme)
        throws URISyntaxException {
        Identifier identifierDto = new Identifier(scheme, "id", name, new URI("str"));
        Address address = new Address("str", "str", "str", "str", "str");
        ContactPoint contactPoint = new ContactPoint("str", "str", "str", "str", new URI("str"),
                                                     Collections.singletonList("str"));
        return new OrganizationReference(name, "id", identifierDto, address, new LinkedHashSet<>(),
                                         contactPoint);
    }
}

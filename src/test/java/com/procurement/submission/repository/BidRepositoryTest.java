package com.procurement.submission.repository;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.submission.JsonUtil;
import com.procurement.submission.model.entity.BidEntity;
import com.procurement.submission.model.ocds.Bid;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BidRepositoryTest {

    private static BidRepository bidRepository;

    private static BidEntity bidEntity;

    @BeforeAll
    static void setUp() {
        bidEntity = new BidEntity();
        bidEntity.setOcId("ocds-213czf-000-00001");
        bidEntity.setBidId(UUIDs.timeBased());
        bidEntity.setStatus(Bid.Status.PENDING);
        bidEntity.setJsonData(new JsonUtil().getResource("json/old/qualification-offer.json"));
        bidRepository = mock(BidRepository.class);
        when(bidRepository.save(bidEntity)).thenReturn(bidEntity);
    }

    @Test
    public void save() {
        BidEntity result = bidRepository.save(bidEntity);
        assertEquals(result.getOcId(), bidEntity.getOcId());
    }
}
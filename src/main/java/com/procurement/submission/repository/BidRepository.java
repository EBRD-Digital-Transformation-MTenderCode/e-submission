package com.procurement.submission.repository;

import com.procurement.submission.model.entity.BidEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends CassandraRepository<BidEntity, String> {

    List<BidEntity> findAllByOcIdAndStage(String ocId, String stage);

    BidEntity findByOcIdAndStageAndToken(String ocid, String stage, String token);

    @Query(value = "SELECT * FROM submission_bid WHERE oc_id=?0 AND stage=?1 AND bid_id=?2 LIMIT 1")
    BidEntity findByOcIdAndStageAndBidId(String ocId, String stage, String bidId);

    @Query(value = "select * from access where oc_id=?0 LIMIT 1")
    BidEntity getLastByOcId(String ocId);

    List<BidEntity> findAllByOcIdAndStageAndBidId(String ocid, String stage, Set<UUID> bidId);

}

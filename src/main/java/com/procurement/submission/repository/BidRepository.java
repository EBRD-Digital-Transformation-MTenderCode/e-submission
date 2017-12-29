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

    @Query(value = "select * from access where oc_id=?0 LIMIT 1")
    BidEntity getLastByOcId(String ocId);

    @Query(value = "SELECT * FROM submission_bid WHERE oc_id=?0 AND stage=?1 AND bid_id=?2 LIMIT 1")
    BidEntity findByOcIdAndStageAndBidId(String oaId, String stage, UUID bidId);

    List<BidEntity> findAllByOcIdAndStage(String ocId, String stage);

    List<BidEntity> findAllByOcIdAndStageAndBidId(String ocid, String stage, Set<UUID> bidId);

    BidEntity findByOcIdAndStageAndBidIdAndBidSignId( String ocid,  String stage, UUID bidId, UUID bidSignId);
}

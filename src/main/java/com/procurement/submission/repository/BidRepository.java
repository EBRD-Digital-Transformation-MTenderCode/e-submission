package com.procurement.submission.repository;

import com.procurement.submission.model.entity.BidEntity;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends CassandraRepository<BidEntity, String> {

    List<BidEntity> findAllByCpIdAndStage(String cpId, String stage);

    BidEntity findByCpIdAndStageAndToken(String cpId, String stage, String token);

    @Query(value = "SELECT * FROM submission_bid WHERE cp_id=?0 AND stage=?1 AND bid_id=?2 LIMIT 1")
    BidEntity findByCpIdAndStageAndBidId(String cpId, String stage, String bidId);
}

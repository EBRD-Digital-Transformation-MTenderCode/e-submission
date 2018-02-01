package com.procurement.submission.repository;

import com.procurement.submission.model.entity.BidEntity;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends CassandraRepository<BidEntity, String> {

    List<BidEntity> findAllByCpIdAndStage(String cpId, String stage);

    BidEntity findByCpIdAndStageAndBidIdAndToken(String cpId, String stage, String bidId, String token);

    BidEntity findByCpIdAndStageAndBidId(String cpId, String stage, String bidId);
}

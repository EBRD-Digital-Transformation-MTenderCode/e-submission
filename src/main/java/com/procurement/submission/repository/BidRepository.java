package com.procurement.submission.repository;

import com.procurement.submission.model.entity.BidEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends CassandraRepository<BidEntity, String> {

    List<BidEntity> findAllByCpIdAndStage(String cpId, String stage);

    BidEntity findByCpIdAndStageAndBidIdAndToken(String cpId, String stage, UUID bidId, UUID token);

    BidEntity findByCpIdAndStageAndBidId(String cpId, String stage, UUID bidId);
}

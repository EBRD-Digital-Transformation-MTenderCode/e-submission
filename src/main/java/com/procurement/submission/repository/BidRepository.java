package com.procurement.submission.repository;

import com.procurement.submission.model.entity.BidEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends CassandraRepository<BidEntity, String> {

    @Query(value = "select * from access where oc_id=?0 LIMIT 1")
    BidEntity getLastByOcId(String ocId);
}

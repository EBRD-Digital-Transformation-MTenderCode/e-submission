package com.procurement.submission.repository;

import com.procurement.submission.model.entity.ContractProcessPeriodEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractProcessPeriodRepository extends CassandraRepository<ContractProcessPeriodEntity, String> {

    @Query(value = "select * from access where tender_id=?0 LIMIT 1")
    ContractProcessPeriodEntity getByTenderId(String tenderId);
}

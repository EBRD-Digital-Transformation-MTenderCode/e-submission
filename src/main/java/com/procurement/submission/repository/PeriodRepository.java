package com.procurement.submission.repository;

import com.procurement.submission.model.entity.PeriodEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodRepository extends CassandraRepository<PeriodEntity, String> {

    @Query(value = "select * from submission_period where cp_id=?0 LIMIT 1")
    Optional<PeriodEntity> getByCpId(String cpId);
}

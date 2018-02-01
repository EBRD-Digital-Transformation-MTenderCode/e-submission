package com.procurement.submission.repository;

import com.procurement.submission.model.entity.PeriodEntity;
import java.util.Optional;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends CassandraRepository<PeriodEntity, String> {

    @Query(value = "select * from submission_period where cp_id=?0 and stage=?1 LIMIT 1")
    Optional<PeriodEntity> getByCpIdAndStage(String cpId, String stage);
}

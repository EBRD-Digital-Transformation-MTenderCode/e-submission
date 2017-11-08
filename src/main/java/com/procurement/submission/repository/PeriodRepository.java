package com.procurement.submission.repository;

import com.procurement.submission.model.entity.SubmissionPeriodEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends CassandraRepository<SubmissionPeriodEntity, String> {

    @Query(value = "select * from submission_period where oc_id=?0 LIMIT 1")
    SubmissionPeriodEntity getByOcId(String ocId);
}

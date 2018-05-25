package com.procurement.submission.repository

import com.procurement.submission.model.entity.BidEntity
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BidRepository : CassandraRepository<BidEntity, String> {

    fun findAllByCpIdAndStage(cpId: String, stage: String): List<BidEntity>

    fun findByCpIdAndStageAndBidId(cpId: String, stage: String, bidId: UUID): BidEntity?
}

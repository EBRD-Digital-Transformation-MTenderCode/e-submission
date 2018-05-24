package com.procurement.submission.repository

import com.procurement.submission.model.entity.BidEntity
import java.util.UUID
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface BidRepository : CassandraRepository<BidEntity, String> {

    fun findAllByCpIdAndStage(cpId: String, stage: String): List<BidEntity>

    fun findByCpIdAndStageAndBidIdAndToken(cpId: String, stage: String, bidId: UUID, token: UUID): BidEntity?

    fun findByCpIdAndStageAndBidId(cpId: String, stage: String, bidId: UUID): BidEntity?
}

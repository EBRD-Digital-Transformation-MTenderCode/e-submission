package com.procurement.submission.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.*
import com.procurement.submission.exception.ErrorException
import com.procurement.submission.exception.ErrorType
import com.procurement.submission.model.entity.BidEntity
import org.springframework.stereotype.Service
import java.util.*

interface BidDao {

    fun save(entity: BidEntity)

    fun saveAll(entities: List<BidEntity>)

    fun findAllByCpIdAndStage(cpId: String, stage: String): List<BidEntity>

    fun findByCpIdAndStageAndBidId(cpId: String, stage: String, bidId: UUID): BidEntity

}

@Service
class BidDaoImpl(private val session: Session) : BidDao {

    override fun save(entity: BidEntity) {
        val insert =
                insertInto(BID_TABLE)
                        .value(CP_ID, entity.cpId)
                        .value(BID_ID, entity.bidId)
                        .value(TOKEN, entity.token)
                        .value(STAGE, entity.stage)
                        .value(OWNER, entity.owner)
                        .value(STATUS, entity.status)
                        .value(CREATED_DATE, entity.createdDate)
                        .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
    }


    override fun saveAll(entities: List<BidEntity>) {
        val operations = ArrayList<Insert>()
        entities.forEach { entity ->
            operations.add(
                    insertInto(BID_TABLE)
                            .value(CP_ID, entity.cpId)
                            .value(BID_ID, entity.bidId)
                            .value(TOKEN, entity.token)
                            .value(STAGE, entity.stage)
                            .value(OWNER, entity.owner)
                            .value(STATUS, entity.status)
                            .value(CREATED_DATE, entity.createdDate)
                            .value(JSON_DATA, entity.jsonData))
        }
        val batch = QueryBuilder.batch(*operations.toTypedArray())
        session.execute(batch)
    }

    override fun findAllByCpIdAndStage(cpId: String, stage: String): List<BidEntity> {
        val query = select()
                .all()
                .from(BID_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(STAGE, stage))
        val resultSet = session.execute(query)
        val entities = ArrayList<BidEntity>()
        resultSet.forEach { row ->
            entities.add(
                    BidEntity(
                            cpId = row.getString(CP_ID),
                            bidId = row.getUUID(BID_ID),
                            token = row.getUUID(TOKEN),
                            stage = row.getString(STAGE),
                            owner = row.getString(OWNER),
                            status = row.getString(STATUS),
                            createdDate = row.getTimestamp(CREATED_DATE),
                            jsonData = row.getString(JSON_DATA)))
        }
        return entities
    }

    override fun findByCpIdAndStageAndBidId(cpId: String, stage: String, bidId: UUID): BidEntity {
        val query = select()
                .all()
                .from(BID_TABLE)
                .where(eq(CP_ID, cpId))
                .and(eq(BID_ID, bidId)).limit(1)
        val row = session.execute(query).one()
        return if (row != null)
            BidEntity(
                    cpId = row.getString(CP_ID),
                    bidId = row.getUUID(BID_ID),
                    token = row.getUUID(TOKEN),
                    stage = row.getString(STAGE),
                    owner = row.getString(OWNER),
                    status = row.getString(STATUS),
                    createdDate = row.getTimestamp(CREATED_DATE),
                    jsonData = row.getString(JSON_DATA))
        else throw ErrorException(ErrorType.BID_NOT_FOUND)
    }

    companion object {
        private val BID_TABLE = "submission_bid"
        private val CP_ID = "cp_id"
        private val STAGE = "stage"
        private val BID_ID = "bid_id"
        private val TOKEN = "token_entity"
        private val OWNER = "owner"
        private val STATUS = "status"
        private val CREATED_DATE = "created_date"
        private val JSON_DATA = "json_data"
    }
}

package com.procurement.submission.infrastructure.repository.bid

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.bid.BidRepository
import com.procurement.submission.application.repository.bid.model.BidEntity
import com.procurement.submission.application.service.Transform
import com.procurement.submission.domain.fail.Fail
import com.procurement.submission.domain.model.Cpid
import com.procurement.submission.domain.model.Ocid
import com.procurement.submission.domain.model.Owner
import com.procurement.submission.domain.model.Token
import com.procurement.submission.domain.model.bid.BidId
import com.procurement.submission.domain.model.enums.Status
import com.procurement.submission.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.submission.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.submission.infrastructure.extension.cassandra.tryExecute
import com.procurement.submission.infrastructure.repository.Database
import com.procurement.submission.lib.functional.MaybeFail
import com.procurement.submission.lib.functional.Result
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import org.springframework.stereotype.Repository

@Repository
class BidRepositoryCassandra(private val session: Session, private val transform: Transform) : BidRepository {

    companion object {

        private const val ID_VALUES = "id_values"

        private const val FIND_BY_CPID_CQL = """
               SELECT ${Database.Bids.CPID},
                      ${Database.Bids.OCID},
                      ${Database.Bids.ID},
                      ${Database.Bids.TOKEN},
                      ${Database.Bids.OWNER},
                      ${Database.Bids.STATUS},
                      ${Database.Bids.CREATED_DATE},
                      ${Database.Bids.PENDING_DATE},
                      ${Database.Bids.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Bids.TABLE}
                WHERE ${Database.Bids.CPID}=?
            """

        private const val FIND_BY_CPID_OCID_CQL = """
               SELECT ${Database.Bids.CPID},
                      ${Database.Bids.OCID},
                      ${Database.Bids.ID},
                      ${Database.Bids.TOKEN},
                      ${Database.Bids.OWNER},
                      ${Database.Bids.STATUS},
                      ${Database.Bids.CREATED_DATE},
                      ${Database.Bids.PENDING_DATE},
                      ${Database.Bids.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Bids.TABLE}
                WHERE ${Database.Bids.CPID}=?
                  AND ${Database.Bids.OCID}=?
            """

        private const val FIND_BY_CPID_OCID_ID_CQL = """
               SELECT ${Database.Bids.CPID},
                      ${Database.Bids.OCID},
                      ${Database.Bids.ID},
                      ${Database.Bids.TOKEN},
                      ${Database.Bids.OWNER},
                      ${Database.Bids.STATUS},
                      ${Database.Bids.CREATED_DATE},
                      ${Database.Bids.PENDING_DATE},
                      ${Database.Bids.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Bids.TABLE}
                WHERE ${Database.Bids.CPID}=?
                  AND ${Database.Bids.OCID}=?
                  AND ${Database.Bids.ID}=?
            """

        private const val FIND_BY_CPID_OCID_IDS_CQL = """
               SELECT ${Database.Bids.CPID},
                      ${Database.Bids.OCID},
                      ${Database.Bids.ID},
                      ${Database.Bids.TOKEN},
                      ${Database.Bids.OWNER},
                      ${Database.Bids.STATUS},
                      ${Database.Bids.CREATED_DATE},
                      ${Database.Bids.PENDING_DATE},
                      ${Database.Bids.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Bids.TABLE}
                WHERE ${Database.Bids.CPID}=?
                  AND ${Database.Bids.OCID}=?
                  AND ${Database.Bids.ID} IN :$ID_VALUES;
            """

        private const val SAVE_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.Bids.TABLE}(
                      ${Database.Bids.CPID},
                      ${Database.Bids.OCID},
                      ${Database.Bids.ID},
                      ${Database.Bids.TOKEN},
                      ${Database.Bids.OWNER},
                      ${Database.Bids.STATUS},
                      ${Database.Bids.CREATED_DATE},
                      ${Database.Bids.PENDING_DATE},
                      ${Database.Bids.JSON_DATA}
               )
               VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val UPDATE_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.Bids.TABLE}
                  SET ${Database.Bids.STATUS}=?,
                      ${Database.Bids.CREATED_DATE}=?,
                      ${Database.Bids.PENDING_DATE}=?,
                      ${Database.Bids.JSON_DATA}=?
                WHERE ${Database.Bids.CPID}=?
                  AND ${Database.Bids.OCID}=?
                  AND ${Database.Bids.ID}=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_OCID_CQL)
    private val preparedFindByCpidAndOcidAndIdCQL = session.prepare(FIND_BY_CPID_OCID_ID_CQL)
    private val preparedFindByCpidAndOcidAndIdsCQL = session.prepare(FIND_BY_CPID_OCID_IDS_CQL)
    private val preparedSaveCQL = session.prepare(SAVE_CQL)
    private val preparedUpdateQL = session.prepare(UPDATE_CQL)

    override fun findBy(cpid: Cpid): Result<List<BidEntity.Record>, Fail.Incident.Database> {
        val query = preparedFindByCpidCQL.bind()
            .apply {
                setString(Database.Bids.CPID, cpid.toString())
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .map { row -> row.convert() }
            .asSuccess()
    }

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<BidEntity.Record>, Fail.Incident.Database> {
        val query = preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.Bids.CPID, cpid.toString())
                setString(Database.Bids.OCID, ocid.toString())
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .map { row -> row.convert() }
            .asSuccess()
    }

    override fun findBy(cpid: Cpid, ocid: Ocid, id: BidId): Result<BidEntity.Record?, Fail.Incident.Database> {
        val query = preparedFindByCpidAndOcidAndIdCQL.bind()
            .apply {
                setString(Database.Bids.CPID, cpid.toString())
                setString(Database.Bids.OCID, ocid.toString())
                setString(Database.Bids.ID, id.toString())
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .one()
            ?.convert()
            .asSuccess()
    }

    override fun findBy(cpid: Cpid, ocid: Ocid, ids: List<BidId>): Result<List<BidEntity.Record>, Fail.Incident.Database> {
        val query = preparedFindByCpidAndOcidAndIdsCQL.bind()
            .apply {
                setString(Database.Bids.CPID, cpid.toString())
                setString(Database.Bids.OCID, ocid.toString())
                setList(Database.Bids.ID, ids.map { it.toString() })
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .map { it.convert() }
            .asSuccess()
    }

    private fun Row.convert() = BidEntity.Record(
        cpid = Cpid.tryCreateOrNull(getString(Database.Bids.CPID))!!,
        ocid = Ocid.tryCreateOrNull(getString(Database.Bids.OCID))!!,
        bidId = BidId.fromString(getString(Database.Bids.ID)),
        token = Token.fromString(getString(Database.Bids.TOKEN)),
        owner = Owner.fromString(getString(Database.Bids.OWNER)),
        status = Status.creator(getString(Database.Bids.STATUS)),
        createdDate = getTimestamp(Database.Bids.CREATED_DATE).toLocalDateTime(),
        pendingDate = getTimestamp(Database.Bids.PENDING_DATE)?.toLocalDateTime(),
        jsonData = getString(Database.Bids.JSON_DATA)
    )

    override fun save(bidEntity: BidEntity): MaybeFail<Fail.Incident.Database> {
        buildStatement(bidEntity)
            .onFailure { return MaybeFail.fail(it.reason) }
            .tryExecute(session)
            .onFailure { return MaybeFail.fail(it.reason) }

        return MaybeFail.none()
    }

    override fun save(bidEntities: Collection<BidEntity>): MaybeFail<Fail.Incident.Database> {
        val batchStatement = BatchStatement()

        bidEntities.forEach { bidEntity ->
            val statement = buildStatement(bidEntity)
                .onFailure { return MaybeFail.fail(it.reason) }
            batchStatement.add(statement)
        }

        batchStatement.tryExecute(session)
            .onFailure { return MaybeFail.fail(it.reason) }

        return MaybeFail.none()
    }

    private fun buildStatement(bidEntity: BidEntity): Result<BoundStatement, Fail.Incident.Database> =
        when (bidEntity) {
            is BidEntity.New -> buildStatement(bidEntity)
            is BidEntity.Updated -> buildStatement(bidEntity)
        }

    private fun buildStatement(entity: BidEntity.New): Result<BoundStatement, Fail.Incident.Database> {
        val data = generateJsonData(entity.bid)
            .onFailure { return it }
        return preparedSaveCQL.bind()
            .apply {
                setString(Database.Bids.CPID, entity.cpid.toString())
                setString(Database.Bids.OCID, entity.ocid.toString())
                setString(Database.Bids.ID, entity.bid.id)
                setString(Database.Bids.TOKEN, entity.token.toString())
                setString(Database.Bids.OWNER, entity.owner.toString())
                setString(Database.Bids.STATUS, entity.bid.status.key)
                setTimestamp(Database.Bids.CREATED_DATE, entity.createdDate.toCassandraTimestamp())
                setTimestamp(Database.Bids.PENDING_DATE, entity.pendingDate?.toCassandraTimestamp())
                setString(Database.Bids.JSON_DATA, data)
            }
            .asSuccess()
    }

    private fun buildStatement(entity: BidEntity.Updated): Result<BoundStatement, Fail.Incident.Database> {
        val data = generateJsonData(entity.bid)
            .onFailure { return it }
        return preparedUpdateQL.bind()
            .apply {
                setString(Database.Bids.CPID, entity.cpid.toString())
                setString(Database.Bids.OCID, entity.ocid.toString())
                setString(Database.Bids.ID, entity.bid.id)
                setString(Database.Bids.STATUS, entity.bid.status.key)
                setTimestamp(Database.Bids.CREATED_DATE, entity.createdDate.toCassandraTimestamp())
                setTimestamp(Database.Bids.PENDING_DATE, entity.pendingDate?.toCassandraTimestamp())
                setString(Database.Bids.JSON_DATA, data)
            }
            .asSuccess()
    }

    private fun generateJsonData(bid: Bid): Result<String, Fail.Incident.Database> =
        transform.trySerialization(bid)
            .mapFailure {
                Fail.Incident.Database.DatabaseParsing(exception = it.exception)
            }
}

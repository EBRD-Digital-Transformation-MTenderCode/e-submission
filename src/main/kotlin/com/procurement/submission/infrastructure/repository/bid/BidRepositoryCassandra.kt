package com.procurement.submission.infrastructure.repository.bid

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.submission.application.repository.bid.BidRepository
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
import com.procurement.submission.lib.functional.asFailure
import com.procurement.submission.lib.functional.asSuccess
import com.procurement.submission.model.dto.ocds.Bid
import com.procurement.submission.model.entity.BidEntityComplex
import org.springframework.stereotype.Repository

@Repository
class BidRepositoryCassandra(private val session: Session, private val transform: Transform) : BidRepository {

    companion object {

        private const val FIND_BY_CQL = """
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

        private const val FIND_BY_ID_CQL = """
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
            """
    }

    private val preparedFindByCQL = session.prepare(FIND_BY_CQL)
    private val preparedFindByIdCQL = session.prepare(FIND_BY_ID_CQL)
    private val preparedSaveCQL = session.prepare(SAVE_CQL)

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<BidEntityComplex>, Fail.Incident> {
        val query = preparedFindByCQL.bind()
            .apply {
                setString(Database.Bids.CPID, cpid.toString())
                setString(Database.Bids.OCID, ocid.toString())
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .map { row -> row.convert().onFailure { return it } }
            .asSuccess()
    }

    override fun findBy(cpid: Cpid, ocid: Ocid, id: BidId): Result<BidEntityComplex?, Fail.Incident> {
        val query = preparedFindByIdCQL.bind()
            .apply {
                setString(Database.Bids.CPID, cpid.toString())
                setString(Database.Bids.OCID, ocid.toString())
                setString(Database.Bids.ID, id.toString())
            }

        return query.tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { row ->
                row.convert().onFailure { return it }
            }
            .asSuccess()
    }

    private fun Row.convert(): Result<BidEntityComplex, Fail.Incident> {
        val data = getString(Database.Bids.JSON_DATA)
        val entity = transform.tryDeserialization(value = data, target = Bid::class.java)
            .onFailure {
                return Fail.Incident.Database.DatabaseParsing(exception = it.reason.exception).asFailure()
            }

        return BidEntityComplex(
            cpid = Cpid.tryCreateOrNull(getString(Database.Bids.CPID))!!,
            ocid = Ocid.tryCreateOrNull(getString(Database.Bids.OCID))!!,
            bidId = BidId.fromString(getString(Database.Bids.ID)),
            token = Token.fromString(getString(Database.Bids.TOKEN)),
            owner = Owner.fromString(getString(Database.Bids.OWNER)),
            status = Status.creator(getString(Database.Bids.STATUS)),
            createdDate = getTimestamp(Database.Bids.CREATED_DATE).toLocalDateTime(),
            pendingDate = getTimestamp(Database.Bids.PENDING_DATE)?.toLocalDateTime(),
            bid = entity
        ).asSuccess()
    }

    override fun saveNew(bidEntity: BidEntityComplex): MaybeFail<Fail.Incident> {
        buildStatement(bidEntity)
            .onFailure { return MaybeFail.fail(it.reason) }
            .tryExecute(session)
            .onFailure { return MaybeFail.fail(it.reason) }

        return MaybeFail.none()
    }

    override fun saveNew(bidEntities: List<BidEntityComplex>): MaybeFail<Fail.Incident> {
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

    private fun buildStatement(bidEntity: BidEntityComplex): Result<BoundStatement, Fail.Incident> {
        val data = generateJsonData(bidEntity.bid)
            .onFailure { return it }

        return preparedSaveCQL.bind()
            .apply {
                setString(Database.Bids.CPID, bidEntity.cpid.toString())
                setString(Database.Bids.OCID, bidEntity.ocid.toString())
                setString(Database.Bids.ID, bidEntity.bid.id.toString())
                setString(Database.Bids.TOKEN, bidEntity.token.toString())
                setString(Database.Bids.OWNER, bidEntity.owner.toString())
                setString(Database.Bids.STATUS, bidEntity.bid.status.key)
                setTimestamp(Database.Bids.CREATED_DATE, bidEntity.createdDate.toCassandraTimestamp())
                setTimestamp(Database.Bids.PENDING_DATE, bidEntity.pendingDate?.toCassandraTimestamp())
                setString(Database.Bids.JSON_DATA, data)
            }
            .asSuccess()
    }

    private fun generateJsonData(bid: Bid): Result<String, Fail.Incident> =
        transform.trySerialization(bid)
            .mapFailure {
                Fail.Incident.Database.DatabaseParsing(exception = it.exception)
            }
}
